package com.kaua.ecommerce.application.gateways;

import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductColor;
import com.kaua.ecommerce.domain.product.ProductSize;

import java.util.Optional;

public interface ProductGateway {

    Product create(Product aProduct);

    Optional<ProductColor> findColorByName(String aColorName);
}
