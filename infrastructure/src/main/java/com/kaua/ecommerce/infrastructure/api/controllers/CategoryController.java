package com.kaua.ecommerce.infrastructure.api.controllers;

import com.kaua.ecommerce.application.usecases.category.create.CreateCategoryRootCommand;
import com.kaua.ecommerce.application.usecases.category.create.CreateCategoryRootUseCase;
import com.kaua.ecommerce.infrastructure.api.CategoryAPI;
import com.kaua.ecommerce.infrastructure.category.models.CreateCategoryInput;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController implements CategoryAPI {

    private final CreateCategoryRootUseCase createCategoryRootUseCase;

    public CategoryController(final CreateCategoryRootUseCase createCategoryRootUseCase) {
        this.createCategoryRootUseCase = createCategoryRootUseCase;
    }

    @Override
    public ResponseEntity<?> createCategory(CreateCategoryInput body) {
        final var aResult = this.createCategoryRootUseCase.execute(
                CreateCategoryRootCommand.with(body.name(), body.description()));

        return aResult.isLeft()
                ? ResponseEntity.unprocessableEntity().body(aResult.getLeft())
                : ResponseEntity.ok().body(aResult.getRight());
    }
}
