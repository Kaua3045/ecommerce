package com.kaua.ecommerce.infrastructure.product.persistence;

import com.kaua.ecommerce.application.gateways.responses.ProductDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductJpaEntityRepository extends JpaRepository<ProductJpaEntity, String> {

    @Query("""
             select new com.kaua.ecommerce.application.gateways.responses.ProductDetails(
                pa.sku,
                p.price,
                ps.weight,
                ps.width,
                ps.height,
                ps.length
             )
             from ProductJpaEntity p
             inner join p.attributes pa
             inner join pa.size ps
             where pa.sku = :sku
            """)
    Optional<ProductDetails> findFirstProductDetailsBySku(String sku);
}
