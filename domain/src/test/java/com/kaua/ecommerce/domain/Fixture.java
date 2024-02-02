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
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public final class Fixture {

    private static final Faker faker = new Faker();

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

        private static final Product TSHIRT = Product.newProduct(
                "Camiseta",
                "Camiseta de algodão",
                BigDecimal.valueOf(10.0),
                50,
                CategoryID.unique(),
                Set.of(ProductAttributes.create(
                        ProductColor.with(faker.color().name()),
                        ProductSize.with(faker.options()
                                        .option("P", "M", "G", "GG", "XG"),
                                faker.random().nextDouble(),
                                faker.random().nextDouble(),
                                faker.random().nextDouble(),
                                faker.random().nextDouble()),
                        "Camiseta"))
        );

        private static final Product BOOK = Product.newProduct(
                "Livro",
                "Livro de teste",
                BigDecimal.valueOf(faker.random().nextDouble(1, 999999)),
                faker.random().nextInt(1, 100),
                CategoryID.unique(),
                Set.of(ProductAttributes.create(
                        ProductColor.with(faker.color().name()),
                        ProductSize.with(faker.options()
                                        .option("M", "G"),
                                faker.random().nextDouble(),
                                faker.random().nextDouble(),
                                faker.random().nextDouble(),
                                faker.random().nextDouble()),
                        "Livro"))
        );

        public static Product tshirt() {
            return Product.with(TSHIRT);
        }

        public static Product book() {
            return Product.with(BOOK);
        }

        public static ProductImageResource productImageResource(final ProductImageType aType) {
            return ProductImageResource.with(
                    resource(),
                    aType
            );
        }

        public static ProductImage productImage(final ProductImageType aType) {
            final var aName = faker.name().title();
            final var aLocation = aType.name().concat("-").concat(aName).concat(".jpg");
            return ProductImage.with(
                    aName,
                    "/images/".concat(aLocation),
                    "https://localhost:8080/".concat(aLocation)
            );
        }

        public static Resource resource() {
            final var aName = faker.name().title();
            return Resource.with(
                    aName.getBytes(),
                    "image/png",
                    aName.concat(".jpg")
            );
        }
    }
}
