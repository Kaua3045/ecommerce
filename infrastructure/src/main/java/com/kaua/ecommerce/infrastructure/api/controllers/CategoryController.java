package com.kaua.ecommerce.infrastructure.api.controllers;

import com.kaua.ecommerce.application.usecases.category.create.CreateCategoryRootCommand;
import com.kaua.ecommerce.application.usecases.category.create.CreateCategoryRootUseCase;
import com.kaua.ecommerce.application.usecases.category.delete.DeleteCategoryUseCase;
import com.kaua.ecommerce.application.usecases.category.retrieve.list.ListCategoriesUseCase;
import com.kaua.ecommerce.application.usecases.category.update.UpdateCategoryCommand;
import com.kaua.ecommerce.application.usecases.category.update.UpdateCategoryUseCase;
import com.kaua.ecommerce.application.usecases.category.update.subcategories.UpdateSubCategoriesCommand;
import com.kaua.ecommerce.application.usecases.category.update.subcategories.UpdateSubCategoriesUseCase;
import com.kaua.ecommerce.domain.pagination.Pagination;
import com.kaua.ecommerce.domain.pagination.SearchQuery;
import com.kaua.ecommerce.infrastructure.api.CategoryAPI;
import com.kaua.ecommerce.infrastructure.category.models.CreateCategoryInput;
import com.kaua.ecommerce.infrastructure.category.models.ListCategoriesResponse;
import com.kaua.ecommerce.infrastructure.category.models.UpdateCategoryInput;
import com.kaua.ecommerce.infrastructure.category.models.UpdateSubCategoriesInput;
import com.kaua.ecommerce.infrastructure.category.presenter.CategoryApiPresenter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController implements CategoryAPI {

    private final CreateCategoryRootUseCase createCategoryRootUseCase;
    private final UpdateSubCategoriesUseCase updateSubCategoriesUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;
    private final ListCategoriesUseCase listCategoriesUseCase;

    public CategoryController(
            final CreateCategoryRootUseCase createCategoryRootUseCase,
            final UpdateSubCategoriesUseCase updateSubCategoriesUseCase,
            final UpdateCategoryUseCase updateCategoryUseCase,
            final DeleteCategoryUseCase deleteCategoryUseCase,
            final ListCategoriesUseCase listCategoriesUseCase
    ) {
        this.createCategoryRootUseCase = createCategoryRootUseCase;
        this.updateSubCategoriesUseCase = updateSubCategoriesUseCase;
        this.updateCategoryUseCase = updateCategoryUseCase;
        this.deleteCategoryUseCase = deleteCategoryUseCase;
        this.listCategoriesUseCase = listCategoriesUseCase;
    }

    @Override
    public ResponseEntity<?> createCategory(CreateCategoryInput body) {
        final var aResult = this.createCategoryRootUseCase.execute(
                CreateCategoryRootCommand.with(body.name(), body.description()));

        return aResult.isLeft()
                ? ResponseEntity.unprocessableEntity().body(aResult.getLeft())
                : ResponseEntity.status(HttpStatus.CREATED).body(aResult.getRight());
    }

    @Override
    public Pagination<ListCategoriesResponse> listCategories(String search, int page, int perPage, String sort, String direction) {
        final var aQuery = new SearchQuery(page, perPage, search, sort, direction);
        return this.listCategoriesUseCase.execute(aQuery)
                .map(CategoryApiPresenter::present);
    }

    @Override
    public ResponseEntity<?> updateSubCategories(String id, UpdateSubCategoriesInput body) {
        final var aResult = this.updateSubCategoriesUseCase.execute(
                UpdateSubCategoriesCommand.with(id, body.name(), body.description()));

        return aResult.isLeft()
                ? ResponseEntity.unprocessableEntity().body(aResult.getLeft())
                : ResponseEntity.ok().body(aResult.getRight());
    }

    @Override
    public ResponseEntity<?> updateCategory(String id, UpdateCategoryInput body) {
        final var aResult = this.updateCategoryUseCase.execute(
                UpdateCategoryCommand.with(id, body.name(), body.description()));

        return aResult.isLeft()
                ? ResponseEntity.unprocessableEntity().body(aResult.getLeft())
                : ResponseEntity.ok().body(aResult.getRight());
    }

    @Override
    public void deleteCategory(String id) {
        this.deleteCategoryUseCase.execute(id);
    }
}
