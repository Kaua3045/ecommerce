package com.kaua.ecommerce.domain;

import com.kaua.ecommerce.domain.category.Category;
import com.kaua.ecommerce.domain.category.CategoryID;
import com.kaua.ecommerce.domain.coupon.Coupon;
import com.kaua.ecommerce.domain.coupon.CouponSlot;
import com.kaua.ecommerce.domain.coupon.CouponType;
import com.kaua.ecommerce.domain.customer.Cpf;
import com.kaua.ecommerce.domain.customer.Customer;
import com.kaua.ecommerce.domain.customer.CustomerID;
import com.kaua.ecommerce.domain.customer.Telephone;
import com.kaua.ecommerce.domain.customer.address.Address;
import com.kaua.ecommerce.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.domain.inventory.Inventory;
import com.kaua.ecommerce.domain.inventory.InventoryID;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovement;
import com.kaua.ecommerce.domain.inventory.movement.InventoryMovementStatus;
import com.kaua.ecommerce.domain.product.*;
import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.domain.utils.Resource;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

public final class Fixture {

    private static final Faker faker = new Faker();

    private Fixture() {
    }

    public static String notFoundMessage(final Class<? extends AggregateRoot<?>> anAggregate, final String id) {
        return NotFoundException.with(anAggregate, id).get().getMessage();
    }

    public static String createSku(final String productName) {
        return ProductAttributes.create(
                ProductColor.with(faker.color().name()),
                ProductSize.with(faker.options()
                                .option("P", "M", "G", "GG", "XG"),
                        faker.random().nextDouble(),
                        faker.random().nextDouble(),
                        faker.random().nextDouble(),
                        faker.random().nextDouble()),
                productName
        ).getSku();
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
            final var aName = faker.name().title().replaceAll(" ", "-");
            final var aLocation = aType.name().concat("-").concat(aName).concat(".jpg");
            return ProductImage.with(
                    aName,
                    "images-".concat(aLocation),
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

        public static ProductAttributes productAttributes(final String aProductName) {
            return ProductAttributes.create(
                    ProductColor.with(faker.color().name()),
                    ProductSize.with(faker.options()
                                    .option("P", "M", "G", "GG", "XG"),
                            faker.random().nextDouble(),
                            faker.random().nextDouble(),
                            faker.random().nextDouble(),
                            faker.random().nextDouble()),
                    aProductName);
        }
    }

    public static final class Inventories {

        private static final Inventory TSHIRT_SKU = Inventory.newInventory(
                ProductID.unique().getValue(),
                "sku-tshirt",
                faker.random().nextInt(1, 500)
        );

        public static Inventory tshirtInventory() {
            return Inventory.with(TSHIRT_SKU);
        }

        public static Inventory createInventoryByProduct(final Product aProduct) {
            return Inventory.newInventory(
                    aProduct.getId().getValue(),
                    aProduct.getAttributes().stream()
                            .findFirst().get().getSku(),
                    faker.random().nextInt(1, 500)
            );
        }
    }

    public static final class InventoriesMovements {

        private static final InventoryMovement IN = InventoryMovement.newInventoryMovement(
                InventoryID.unique(),
                "sku-tshirt",
                faker.random().nextInt(1, 500),
                InventoryMovementStatus.IN
        );

        private static final InventoryMovement OUT = InventoryMovement.newInventoryMovement(
                InventoryID.unique(),
                "sku-tshirt",
                faker.random().nextInt(1, 500),
                InventoryMovementStatus.OUT
        );

        private static final InventoryMovement REMOVED = InventoryMovement.newInventoryMovement(
                InventoryID.unique(),
                "sku-tshirt",
                faker.random().nextInt(1, 500),
                InventoryMovementStatus.REMOVED
        );

        public static InventoryMovement in() {
            return InventoryMovement.with(IN);
        }

        public static InventoryMovement out() {
            return InventoryMovement.with(OUT);
        }

        public static InventoryMovement removed() {
            return InventoryMovement.with(REMOVED);
        }
    }

    public static final class Coupons {

        private static final Coupon UNLIMITED_COUPON_ACTIVATED = Coupon.newCoupon(
                faker.options()
                        .option("BLACK_FRIDAY", "CYBER_MONDAY", "CHRISTMAS", "NEW_YEAR", "EASTER"),
                (float) Math.round(faker.random().nextDouble(1, 100)),
                InstantUtils.now().plus(1, ChronoUnit.DAYS),
                true,
                CouponType.UNLIMITED
        );

        private static final Coupon UNLIMITED_COUPON_DEACTIVATED = Coupon.newCoupon(
                faker.options()
                        .option("BLACK_FRIDAY", "CYBER_MONDAY", "CHRISTMAS", "NEW_YEAR", "EASTER"),
                (float) Math.round(faker.random().nextDouble(1, 100)),
                InstantUtils.now().plus(1, ChronoUnit.DAYS),
                false,
                CouponType.UNLIMITED
        );

        private static final Coupon LIMITED_COUPON_ACTIVATED = Coupon.newCoupon(
                faker.options()
                        .option("BLACK_FRIDAY", "CYBER_MONDAY", "CHRISTMAS", "NEW_YEAR", "EASTER"),
                (float) Math.round(faker.random().nextDouble(1, 100)),
                InstantUtils.now().plus(1, ChronoUnit.DAYS),
                true,
                CouponType.LIMITED
        );

        private static final Coupon LIMITED_COUPON_DEACTIVATED = Coupon.newCoupon(
                faker.options()
                        .option("BLACK_FRIDAY", "CYBER_MONDAY", "CHRISTMAS", "NEW_YEAR", "EASTER"),
                (float) Math.round(faker.random().nextDouble(1, 100)),
                InstantUtils.now().plus(1, ChronoUnit.DAYS),
                false,
                CouponType.LIMITED
        );

        public static Coupon unlimitedCouponActivated() {
            return Coupon.with(UNLIMITED_COUPON_ACTIVATED);
        }

        public static Coupon unlimitedCouponDeactivated() {
            return Coupon.with(UNLIMITED_COUPON_DEACTIVATED);
        }

        public static Coupon limitedCouponActivated() {
            return Coupon.with(LIMITED_COUPON_ACTIVATED);
        }

        public static Coupon limitedCouponDeactivated() {
            return Coupon.with(LIMITED_COUPON_DEACTIVATED);
        }

        public static CouponSlot generateValidCouponSlot(final Coupon aCoupon) {
            return CouponSlot.newCouponSlot(
                    aCoupon.getId().getValue()
            );
        }
    }
}
