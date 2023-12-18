package com.kaua.ecommerce.infrastructure.configurations.usecases;

import com.kaua.ecommerce.application.gateways.CategoryGateway;
import com.kaua.ecommerce.application.gateways.SearchGateway;
import com.kaua.ecommerce.application.usecases.category.create.CreateCategoryRootUseCase;
import com.kaua.ecommerce.application.usecases.category.create.DefaultCreateCategoryRootUseCase;
import com.kaua.ecommerce.application.usecases.category.delete.DefaultDeleteCategoryUseCase;
import com.kaua.ecommerce.application.usecases.category.delete.DeleteCategoryUseCase;
import com.kaua.ecommerce.application.usecases.category.search.remove.DefaultRemoveCategoryUseCase;
import com.kaua.ecommerce.application.usecases.category.search.retrieve.get.DefaultGetCategoryByIdUseCase;
import com.kaua.ecommerce.application.usecases.category.search.retrieve.list.DefaultListCategoriesUseCase;
import com.kaua.ecommerce.application.usecases.category.search.retrieve.list.ListCategoriesUseCase;
import com.kaua.ecommerce.application.usecases.category.search.save.DefaultSaveCategoryUseCase;
import com.kaua.ecommerce.application.usecases.category.update.DefaultUpdateCategoryUseCase;
import com.kaua.ecommerce.application.usecases.category.update.UpdateCategoryUseCase;
import com.kaua.ecommerce.application.usecases.category.update.subcategories.DefaultUpdateSubCategoriesUseCase;
import com.kaua.ecommerce.application.usecases.category.update.subcategories.UpdateSubCategoriesUseCase;
import com.kaua.ecommerce.domain.category.Category;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class CategoryUseCaseConfig {

    private final CategoryGateway categoryGateway;
    private final SearchGateway<Category> searchCategoryGateway;

    public CategoryUseCaseConfig(
            final CategoryGateway categoryGateway,
            final SearchGateway<Category> searchCategoryGateway
    ) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.searchCategoryGateway = Objects.requireNonNull(searchCategoryGateway);
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

    @Bean
    public DefaultSaveCategoryUseCase saveCategoryUseCase() {
        return new DefaultSaveCategoryUseCase(searchCategoryGateway);
    }

    @Bean
    public ListCategoriesUseCase listCategoriesUseCase() {
        return new DefaultListCategoriesUseCase(searchCategoryGateway);
    }

    @Bean
    public DefaultGetCategoryByIdUseCase defaultGetCategoryByIdUseCase() {
        return new DefaultGetCategoryByIdUseCase(searchCategoryGateway);
    }

    @Bean
    public DefaultRemoveCategoryUseCase removeCategoryUseCase() {
        return new DefaultRemoveCategoryUseCase(searchCategoryGateway);
    }
}
