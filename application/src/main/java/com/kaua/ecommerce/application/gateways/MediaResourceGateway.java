package com.kaua.ecommerce.application.gateways;

import com.kaua.ecommerce.domain.product.ProductID;
import com.kaua.ecommerce.domain.product.ProductImage;
import com.kaua.ecommerce.domain.product.ProductImageResource;

public interface MediaResourceGateway {

    ProductImage storeImage(ProductID aProductID, ProductImageResource aResource);

    void clearImage(ProductImage aImage);
}
