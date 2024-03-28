package com.kaua.ecommerce.application.gateways;

import com.kaua.ecommerce.application.gateways.responses.ProductDetails;
import com.kaua.ecommerce.domain.product.Product;
import com.kaua.ecommerce.domain.product.ProductColor;

import java.util.Optional;

public interface ProductGateway {

    Product create(Product aProduct);

    Optional<Product> findById(String aProductID);

    Optional<ProductColor> findColorByName(String aColorName);

    Optional<ProductDetails> findProductDetailsBySku(String aSku);

    Product update(Product aProduct);

    void delete(String aProductID);
}
