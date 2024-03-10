package com.kaua.ecommerce.infrastructure.coupon;

import com.kaua.ecommerce.application.gateways.CouponGateway;
import com.kaua.ecommerce.domain.coupon.Coupon;
import com.kaua.ecommerce.domain.coupon.CouponType;
import com.kaua.ecommerce.domain.pagination.Pagination;
import com.kaua.ecommerce.domain.pagination.Period;
import com.kaua.ecommerce.domain.pagination.SearchQuery;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntity;
import com.kaua.ecommerce.infrastructure.coupon.persistence.CouponJpaEntityRepository;
import com.kaua.ecommerce.infrastructure.utils.SpecificationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

@Component
public class CouponMySQLGateway implements CouponGateway {

    private static final Logger log = LoggerFactory.getLogger(CouponMySQLGateway.class);

    private final CouponJpaEntityRepository couponJpaEntityRepository;

    public CouponMySQLGateway(final CouponJpaEntityRepository couponJpaEntityRepository) {
        this.couponJpaEntityRepository = Objects.requireNonNull(couponJpaEntityRepository);
    }

    @Override
    public Coupon create(final Coupon coupon) {
        final var aResult = this.couponJpaEntityRepository.save(CouponJpaEntity.toEntity(coupon))
                .toDomain();

        log.info("inserted coupon: {}", aResult);
        return aResult;
    }

    @Override
    public boolean existsByCode(final String code) {
        return this.couponJpaEntityRepository.existsByCode(code);
    }

    @Override
    public Coupon update(Coupon coupon) {
        final var aResult = this.couponJpaEntityRepository.save(CouponJpaEntity.toEntity(coupon))
                .toDomain();

        log.info("updated coupon: {}", aResult);
        return aResult;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Coupon> findById(String id) {
        return this.couponJpaEntityRepository.findById(id)
                .map(CouponJpaEntity::toDomain);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Coupon> findByCode(String code) {
        return this.couponJpaEntityRepository.findByCode(code)
                .map(CouponJpaEntity::toDomain);
    }

    @Override
    public Pagination<Coupon> findAll(SearchQuery aQuery) {
        final var aPageRequest = PageRequest.of(
                aQuery.page(),
                aQuery.perPage(),
                Sort.by(Sort.Direction.fromString(aQuery.direction()), aQuery.sort())
        );

        final var aSpecificationTerms = Optional.ofNullable(aQuery.terms())
                .filter(str -> !str.isBlank())
                .map(this::assembleSpecification)
                .orElse(null);

        final var aSpecificationBetween = Optional.ofNullable(aQuery.period())
                .map(this::assembleSpecificationBetween)
                .orElse(null);

        final var aSpecification = Specification.where(aSpecificationBetween)
                .and(aSpecificationTerms);

        final var aPage = this.couponJpaEntityRepository.findAll(aSpecification, aPageRequest);

        return new Pagination<>(
                aPage.getNumber(),
                aPage.getSize(),
                aPage.getTotalPages(),
                aPage.getTotalElements(),
                aPage.map(CouponJpaEntity::toDomain).stream().toList()
        );
    }

    @Override
    public void deleteById(String id) {
        if (this.couponJpaEntityRepository.existsById(id)) {
            this.couponJpaEntityRepository.deleteById(id);
            log.info("deleted coupon with id: {}", id);
        }
    }

    private Specification<CouponJpaEntity> assembleSpecification(final String terms) {
        final Specification<CouponJpaEntity> codeLike = SpecificationUtils.like("code", terms);

        final var aCouponType = CouponType.of(terms).orElse(null);
        final Specification<CouponJpaEntity> typeLike = SpecificationUtils.whereEqual("type", aCouponType);
        return codeLike.or(typeLike);
    }

    private Specification<CouponJpaEntity> assembleSpecificationBetween(final Period aPeriod) {
        if (aPeriod.startDateInstant().isEmpty() && aPeriod.endDateInstant().isEmpty()) {
            return null;
        }

        final var aStartDate = aPeriod.startDateInstant()
                .orElse(InstantUtils.now().minus(30, ChronoUnit.DAYS));
        final var aEndDate = aPeriod.endDateInstant()
                .orElse(InstantUtils.now());

        return SpecificationUtils.between("expirationDate", aStartDate, aEndDate);
    }
}
