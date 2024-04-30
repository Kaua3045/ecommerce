package com.kaua.ecommerce.infrastructure.idempotency;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.infrastructure.IntegrationTest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashMap;
import java.util.Optional;

@IntegrationTest
@AutoConfigureMockMvc
public class IdempotencyKeyFilterTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IdempotencyKeyGateway idempotencyKeyGateway;

    @Test
    void givenAValidPostMethodWithValidNonExistsIdempotencyKey_whenCallEndpoint_thenReturnSuccess() throws Exception {
        final var aId = IdUtils.generateWithoutDash();

        final var aHeaders = new HashMap<String, String>();
        aHeaders.put("Location", "/test/idempotency-key-helper/" + aId);
        aHeaders.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        final var aOutput = new IdempotencyKeyOutput(201, "{\"id\": \"" + aId + "\"}", aHeaders);

        final var aBody = new IdempotencyKeyBodyTest(aId);

        Mockito.when(idempotencyKeyGateway.get(Mockito.anyString()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(aOutput));

        Mockito.doNothing().when(idempotencyKeyGateway)
                .save(Mockito.anyString(), Mockito.any(), Mockito.anyLong(), Mockito.any());

        final var request = MockMvcRequestBuilders.post("/test/idempotency-key-helper/success")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("x-idempotency-key", "123456")
                .content(this.mapper.writeValueAsString(aBody));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location", "/test/idempotency-key-helper/" + aId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(aId));
    }

    @Test
    void givenAValidPostMethodWithValidExistsIdempotencyKey_whenCallEndpoint_thenReturnSuccess() throws Exception {
        final var aId = IdUtils.generateWithoutDash();

        final var aHeaders = new HashMap<String, String>();
        aHeaders.put("Location", "/test/idempotency-key-helper/" + aId);
        aHeaders.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        final var aOutput = new IdempotencyKeyOutput(201, "{\"id\": \"" + aId + "\"}", aHeaders);

        final var aBody = new IdempotencyKeyBodyTest(aId);

        Mockito.when(idempotencyKeyGateway.get(Mockito.anyString()))
                .thenReturn(Optional.of(aOutput));

        final var request = MockMvcRequestBuilders.post("/test/idempotency-key-helper/success")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("x-idempotency-key", "123456")
                .content(this.mapper.writeValueAsString(aBody));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location", "/test/idempotency-key-helper/" + aId))
                .andExpect(MockMvcResultMatchers.header().string("x-idempotency-response", "true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(aId));
    }

    @Test
    void givenAValidGetMethodWithoutIdempotencyKeyAnnotation_whenCallEndpoint_thenReturnSuccess() throws Exception {
        final var aId = IdUtils.generateWithoutDash();

        final var request = MockMvcRequestBuilders.get("/test/idempotency-key-helper/" + aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(aId));
    }

    @Test
    void givenAValidPatchMethodWithValidNonExistsIdempotencyKey_whenCallEndpoint_thenReturnSuccess() throws Exception {
        final var aId = IdUtils.generateWithoutDash();

        final var aHeaders = new HashMap<String, String>();
        aHeaders.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        final var aOutput = new IdempotencyKeyOutput(200, "{\"id\": \"" + aId + "\"}", aHeaders);

        final var aBody = new IdempotencyKeyBodyTest(aId);

        Mockito.when(idempotencyKeyGateway.get(Mockito.anyString()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(aOutput));

        Mockito.doNothing().when(idempotencyKeyGateway)
                .save(Mockito.anyString(), Mockito.any(), Mockito.anyLong(), Mockito.any());

        final var request = MockMvcRequestBuilders.patch("/test/idempotency-key-helper/patch/" + aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("x-idempotency-key", "123456")
                .content(this.mapper.writeValueAsString(aBody));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(aId));
    }

    @Test
    void givenAValidPutMethodWithValidNonExistsIdempotencyKey_whenCallEndpoint_thenReturnSuccess() throws Exception {
        final var aId = IdUtils.generateWithoutDash();

        final var aHeaders = new HashMap<String, String>();
        aHeaders.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        final var aOutput = new IdempotencyKeyOutput(200, "{\"id\": \"" + aId + "\"}", aHeaders);

        final var aBody = new IdempotencyKeyBodyTest(aId);

        Mockito.when(idempotencyKeyGateway.get(Mockito.anyString()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(aOutput));

        Mockito.doNothing().when(idempotencyKeyGateway)
                .save(Mockito.anyString(), Mockito.any(), Mockito.anyLong(), Mockito.any());

        final var request = MockMvcRequestBuilders.put("/test/idempotency-key-helper/put/idempotency/" + aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("x-idempotency-key", "123456")
                .content(this.mapper.writeValueAsString(aBody));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(aId));
    }

    @Test
    void givenASupportedPutMethodButWithoutIdempotencyKeyAnnotation_whenCallEndpoint_thenReturnSuccess() throws Exception {
        final var aId = IdUtils.generateWithoutDash();

        final var aBody = new IdempotencyKeyBodyTest(aId);

        final var request = MockMvcRequestBuilders.put("/test/idempotency-key-helper/put/" + aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aBody));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(aId));
    }

    @Test
    void givenAValidPutMethodButWithoutIdempotencyHeader_whenCallEndpoint_thenReturnError() throws Exception {
        final var aId = IdUtils.generateWithoutDash();

        final var aBody = new IdempotencyKeyBodyTest(aId);

        final var request = MockMvcRequestBuilders.put("/test/idempotency-key-helper/put/idempotency/" + aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aBody));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Idempotency key required and the required header is 'x-idempotency-key'"));
    }

    @Test
    void givenAnInvalidGetMethodWithIdempotencyKeyAnnotation_whenCallEndpoint_thenReturnError() throws Exception {
        final var aId = IdUtils.generateWithoutDash();

        final var request = MockMvcRequestBuilders.get("/test/idempotency-key-helper/get/" + aId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("An internal server error occurred"));
    }

    @Test
    void testOnHandlerMethodThrows() throws Exception {
        final var aRequest = Mockito.mock(HttpServletRequest.class);
        final var aResponse = Mockito.mock(HttpServletResponse.class);
        final var aFilterChain = Mockito.mock(FilterChain.class);

        final var aHandlerExceptionResolver = Mockito.spy(HandlerExceptionResolver.class);
        final var aRequestMappingHandlerMapping = Mockito.mock(RequestMappingHandlerMapping.class);

        final var aIdempotencyKeyFilter = new IdempotencyKeyFilter(
                idempotencyKeyGateway,
                aRequestMappingHandlerMapping,
                aHandlerExceptionResolver
        );

        Mockito.when(aRequestMappingHandlerMapping.getHandler(aRequest))
                .thenThrow(new RuntimeException("test"));

        aIdempotencyKeyFilter.doFilterInternal(aRequest, aResponse, aFilterChain);

        Mockito.verify(aHandlerExceptionResolver, Mockito.times(1))
                .resolveException(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void testOnHandlerMethodReturnsNull() throws Exception {
        final var aRequest = Mockito.mock(HttpServletRequest.class);
        final var aResponse = Mockito.mock(HttpServletResponse.class);
        final var aFilterChain = Mockito.mock(FilterChain.class);

        final var aHandlerExceptionResolver = Mockito.spy(HandlerExceptionResolver.class);
        final var aRequestMappingHandlerMapping = Mockito.mock(RequestMappingHandlerMapping.class);

        final var aIdempotencyKeyFilter = new IdempotencyKeyFilter(
                idempotencyKeyGateway,
                aRequestMappingHandlerMapping,
                aHandlerExceptionResolver
        );

        Mockito.when(aRequestMappingHandlerMapping.getHandler(aRequest))
                .thenReturn(null);

        Assertions.assertDoesNotThrow(() -> aIdempotencyKeyFilter.doFilterInternal(aRequest, aResponse, aFilterChain));
    }

    @Test
    void testOnHandlerMethodIsNotHandlerMethod() throws Exception {
        final var aRequest = Mockito.mock(HttpServletRequest.class);
        final var aResponse = Mockito.mock(HttpServletResponse.class);
        final var aFilterChain = Mockito.mock(FilterChain.class);

        final var aHandlerExceptionResolver = Mockito.spy(HandlerExceptionResolver.class);
        final var aRequestMappingHandlerMapping = Mockito.mock(RequestMappingHandlerMapping.class);

        final var aIdempotencyKeyFilter = new IdempotencyKeyFilter(
                idempotencyKeyGateway,
                aRequestMappingHandlerMapping,
                aHandlerExceptionResolver
        );

        final var aHandlerExecutionChain = Mockito.mock(HandlerExecutionChain.class);

        Mockito.when(aRequestMappingHandlerMapping.getHandler(aRequest))
                .thenReturn(aHandlerExecutionChain);

        Assertions.assertDoesNotThrow(() -> aIdempotencyKeyFilter.doFilterInternal(aRequest, aResponse, aFilterChain));
    }
}
