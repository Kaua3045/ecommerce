package com.kaua.ecommerce.infrastructure.configurations.usecases;

import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.application.usecases.category.create.CreateCategoryRootUseCase;
import com.kaua.ecommerce.application.usecases.category.create.DefaultCreateCategoryRootUseCase;
import com.kaua.ecommerce.application.usecases.category.delete.DefaultDeleteCategoryUseCase;
import com.kaua.ecommerce.application.usecases.category.delete.DeleteCategoryUseCase;
import com.kaua.ecommerce.application.usecases.category.update.DefaultUpdateCategoryUseCase;
import com.kaua.ecommerce.application.usecases.category.update.UpdateCategoryUseCase;
import com.kaua.ecommerce.application.usecases.category.update.subcategories.DefaultUpdateSubCategoriesUseCase;
import com.kaua.ecommerce.application.usecases.category.update.subcategories.UpdateSubCategoriesUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class CategoryUseCaseConfig {

    private final CategoryGateway categoryGateway;

    public CategoryUseCaseConfig(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Bean
    public CreateCategoryRootUseCase createCategoryUseCase() {
        return new DefaultCreateCategoryRootUseCase(categoryGateway);
    }

    @Bean
    public UpdateSubCategoriesUseCase updateSubCategoriesUseCase() {
        return new DefaultUpdateSubCategoriesUseCase(categoryGateway);
    }

    @Bean
    public UpdateCategoryUseCase updateCategoryUseCase() {
        return new DefaultUpdateCategoryUseCase(categoryGateway);
    }

    @Bean
    public DeleteCategoryUseCase deleteCategoryUseCase() {
        return new DefaultDeleteCategoryUseCase(categoryGateway);
    }
}
