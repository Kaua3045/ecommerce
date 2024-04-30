package com.kaua.ecommerce.infrastructure.idempotency;

import com.kaua.ecommerce.infrastructure.exceptions.IdempotencyKeyRequiredException;
import com.kaua.ecommerce.infrastructure.exceptions.IdempotencyKeyUnsupportedMethodException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class IdempotencyKeyFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyKeyFilter.class);

    private final IdempotencyKeyGateway idempotencyKeyGateway;
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final HandlerExceptionResolver resolver;

    public IdempotencyKeyFilter(
            final IdempotencyKeyGateway idempotencyKeyGateway,
            final RequestMappingHandlerMapping requestMappingHandlerMapping,
            final @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver
    ) {
        this.idempotencyKeyGateway = Objects.requireNonNull(idempotencyKeyGateway);
        this.requestMappingHandlerMapping = Objects.requireNonNull(requestMappingHandlerMapping);
        this.resolver = Objects.requireNonNull(resolver);
    }

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) {
        log.debug("Processing the idempotency key filter");
        try {
            final var aHandlerMethod = getHandlerMethod(request);

            if (aHandlerMethod != null && isIdempotencyKeyAnnotated(aHandlerMethod)) {
                if (!isSupportedMethod(request)) {
                    throw new IdempotencyKeyUnsupportedMethodException(request.getMethod());
                }

                final var aIdempotencyKey = request.getHeader(IdempotencyKey.IDEMPOTENCY_KEY_HEADER);

                if (!StringUtils.hasText(aIdempotencyKey)) {
                    throw new IdempotencyKeyRequiredException();
                }

                final var aExistsIdempotencyKey = this.idempotencyKeyGateway.get(aIdempotencyKey);

                if (aExistsIdempotencyKey.isPresent()) {
                    response.setStatus(aExistsIdempotencyKey.get().getStatusCode());
                    aExistsIdempotencyKey.get().getHeaders().forEach(response::addHeader);
                    response.getWriter().write(aExistsIdempotencyKey.get().getBody());
                    response.addHeader(IdempotencyKey.IDEMPOTENCY_RESPONSE_HEADER, "true");
                    log.debug("Idempotency key found, returning the previous response {}", aExistsIdempotencyKey.get());
                    return;
                }

                final var aIdempotencyKeyValues = getIdempotencyKeyValues(aHandlerMethod);
                final var aTTL = aIdempotencyKeyValues.ttl();
                final var aTimeUnit = aIdempotencyKeyValues.timeUnit();

                log.debug("Idempotency key not found, saving before processing the request [key:{}] [ttl:{}] [timeUnit:{}]",
                        aIdempotencyKey, aTTL, aTimeUnit);

                this.idempotencyKeyGateway.save(aIdempotencyKey, aTTL, aTimeUnit);

                final var responseWrapper = new ContentCachingResponseWrapper(response);
                filterChain.doFilter(request, responseWrapper);

                final var aBodyArray = responseWrapper.getContentAsByteArray();
                responseWrapper.copyBodyToResponse();

                final var aBody = new String(aBodyArray, responseWrapper.getCharacterEncoding());
                final var aHeaders = responseWrapper.getHeaderNames().stream()
                        .collect(Collectors.toMap(
                                headerName -> headerName,
                                responseWrapper::getHeader
                        ));

                final var aInput = new IdempotencyKeyInput(
                        responseWrapper.getStatus(),
                        aBody,
                        aHeaders
                );

                this.idempotencyKeyGateway.save(
                        aIdempotencyKey,
                        aInput,
                        aTTL,
                        aTimeUnit
                );

                log.debug("Idempotency key not found, saving the response for future requests [key:{}] [ttl:{}] [timeUnit:{}], result: {}",
                        aIdempotencyKey, aTTL, aTimeUnit, aInput);
            } else {
                filterChain.doFilter(request, response);
            }
        } catch (final Exception e) {
            resolver.resolveException(request, response, null, e);
        }
    }

    private HandlerMethod getHandlerMethod(final HttpServletRequest request) {
        // Obtain the handler method from the request, if available
        final HandlerExecutionChain handlerChain;
        try {
            handlerChain = this.requestMappingHandlerMapping.getHandler(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (handlerChain != null && handlerChain.getHandler() instanceof HandlerMethod) {
            return (HandlerMethod) handlerChain.getHandler();
        }
        return null;
    }

    private boolean isIdempotencyKeyAnnotated(final HandlerMethod handlerMethod) {
        Method method = handlerMethod.getMethod();
        return method.isAnnotationPresent(IdempotencyKey.class);
    }

    private IdempotencyKey getIdempotencyKeyValues(final HandlerMethod handlerMethod) {
        return handlerMethod.getMethodAnnotation(IdempotencyKey.class);
    }

    private boolean isSupportedMethod(final HttpServletRequest request) {
        return request.getMethod().equals("POST") ||
                request.getMethod().equals("PATCH") ||
                request.getMethod().equals("PUT");
    }
}
