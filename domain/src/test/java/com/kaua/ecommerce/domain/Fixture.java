package com.kaua.ecommerce.domain;

import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.customer.Cpf;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.customer.CustomerID;
import com.kaua.ecommerce.domain.customer.Telephone;
import com.kaua.ecommerce.domain.customer.address.Address;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.product.*;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.domain.utils.Resource;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public final class Fixture {

    private Fixture() {
    }

    public static String notFoundMessage(final Class<? extends AggregateRoot<?>> anAggregate, final String id) {
        return NotFoundException.with(anAggregate, id).get().getMessage();
    }

    public static final class Customers {

        private static final String firstName = "Mans";
        private static final String lastName = "Frond";
        private static final String email = "mans.frond@tesss.com";

        public static final Customer customerDefault =
                Customer.newCustomer(
                        IdUtils.generate(),
                        firstName,
                        lastName,
                        email);

        public static final Customer customerWithCpf =
                Customer.newCustomer(
                                IdUtils.generate(),
                                firstName,
                                lastName,
                                email)
                        .changeCpf(Cpf.newCpf("815.959.150-01"));

        public static final Customer customerWithTelephoneAndCpf =
                Customer.newCustomer(
                                IdUtils.generate(),
                                firstName,
                                lastName,
                                email)
                        .changeTelephone(Telephone.newTelephone("+11234567890"))
                        .changeCpf(Cpf.newCpf("815.959.150-01"));

        public static final Customer customerWithAllParams =
                Customer.newCustomer(
                                IdUtils.generate(),
                                firstName,
                                lastName,
                                email)
                        .changeTelephone(Telephone.newTelephone("+15551234567"))
                        .changeCpf(Cpf.newCpf("815.959.150-01"))
                        .changeAddress(Address.newAddress(
                                "Rua Teste",
                                "270",
                                "apt 123",
                                "Bairro",
                                "Cidade",
                                "Estado",
                                "12345678",
                                CustomerID.unique()));
    }

    public static final class Addresses {

        public static final Address addressDefault = Address.newAddress(
                "Rua Teste",
                "123",
                "Complemento",
                "Bairro Teste",
                "Cidade Teste",
                "Estado Teste",
                "12345678",
                CustomerID.unique()
        );
    }

    public static final class Categories {

        public static Category home() {
            return Category.newCategory(
                    "Home Cosmetics",
                    "Home category",
                    "home-cosmetics",
                    null
            );
        }

        public static Category tech() {
            return Category.newCategory(
                    "Tech",
                    "Tech category",
                    "tech",
                    null
            );
        }

        public static Category televisionsWithSubCategories() {
            final var aTelevisionsCategory = Category.newCategory(
                    "Televisions",
                    "Televisions category",
                    "televisions",
                    null
            );
            final var subCategories = makeSubCategories(3, aTelevisionsCategory);
            aTelevisionsCategory.addSubCategories(subCategories);
            aTelevisionsCategory.updateSubCategoriesLevel();
            return aTelevisionsCategory;
        }

        public static Set<Category> makeSubCategories(final int size, final Category parent) {
            var subCategories = new HashSet<Category>();

            for (int i = 0; i < size; i++) {
                subCategories.add(Category.newCategory(
                        "Category Name " + i,
                        "Category Description " + i,
                        "category-name-" + i,
                        parent.getId()
                ));
            }
            return subCategories;
        }
    }

    public static final class Products {

        private static final Product tshirt = Product.newProduct(
                "Camiseta",
                "Camiseta de algodão",
                BigDecimal.valueOf(10.0),
                50,
                CategoryID.unique(),
                Set.of(ProductAttributes.create(
                        ProductColor.with("1", "Red"),
                        ProductSize.with("1", "M", 0.5, 0.5, 0.5, 0.5),
                        "Camiseta"))
        );

        private static final ProductImage imageBannerType = ProductImage.with(
                IdUtils.generate(),
                "image-one.jpg",
                "BANNER-image-one.jpg",
                "https://localhost.com/BANNER-image-one.jpg"
        );

        private static final ProductImageResource imageBannerTypeResource = ProductImageResource.with(
                Resource.with(
                        "image-one".getBytes(),
                        "image/png",
                        "image-one.jpg"
                ),
                ProductImageType.BANNER
        );

        private static final ProductImage imageGalleryType = ProductImage.with(
                IdUtils.generate(),
                "image-two.jpg",
                "GALLERY-image-one.jpg",
                "https://localhost.com/GALLERY-image-one.jpg"
        );

        private static final ProductImageResource imageGalleryTypeResource = ProductImageResource.with(
                Resource.with(
                        "image-two".getBytes(),
                        "image/png",
                        "image-two.jpg"
                ),
                ProductImageType.GALLERY
        );

        public static Product tshirt() {
            return Product.with(tshirt);
        }

        public static ProductImage imageBannerType() {
            return ProductImage.with(
                    imageBannerType.id(),
                    imageBannerType.name(),
                    imageBannerType.location(),
                    imageBannerType.url()
            );
        }

        public static ProductImageResource imageBannerTypeResource() {
            return ProductImageResource.with(
                    imageBannerTypeResource.resource(),
                    imageBannerTypeResource.type()
            );
        }

        public static ProductImage imageGalleryType() {
            return ProductImage.with(
                    imageGalleryType.id(),
                    imageGalleryType.name(),
                    imageGalleryType.location(),
                    imageGalleryType.url()
            );
        }

        public static ProductImageResource imageGalleryTypeResource() {
            return ProductImageResource.with(
                    imageGalleryTypeResource.resource(),
                    imageGalleryTypeResource.type()
            );
        }
    }
}
