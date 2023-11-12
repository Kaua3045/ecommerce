package com.kaua.ecommerce.infrastructure.api.controllers;

import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.infrastructure.category.CategoryEntity;
import com.kaua.ecommerce.infrastructure.category.CategoryGetDTO;
import com.kaua.ecommerce.infrastructure.category.CategoryRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @PostMapping
    public ResponseEntity<?> createCategory() {
        final var id = categoryRepository.save(new CategoryEntity(
                IdUtils.generate(),
                "Vestuario",
                "Roupas e acessórios",
                true,
                null,
                InstantUtils.now(),
                InstantUtils.now()
        ));
        System.out.println(id.getId());

//        categoryRepository.saveAll(List.of(
//                new CategoryEntity(
//                        IdUtils.generate(),
//                        "Console",
//                        null,
//                        null,
//                        InstantUtils.now(),
//                        InstantUtils.now()
//                        )));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/sub")
    public ResponseEntity<?> subCategory() {
        // principal category (console)
        var ab = this.categoryRepository.findById("6a72fde8-7c0f-48c0-92a1-bac6bd97dbc6").get();

        ab.getSubCategories().add(new CategoryEntity(
                IdUtils.generate(),
                "Blusas",
                null,
                false,
                null,
                InstantUtils.now(),
                InstantUtils.now()
        ));

        // playstation category
//        final var playCategory = new CategoryEntity(
//                IdUtils.generate(),
//                "Playstation",
//                "video games",
//                false,
//                Set.of(
//                        new CategoryEntity(
//                                IdUtils.generate(),
//                                "Playstation5",
//                                null,
//                                false,
//                                null,
//                                InstantUtils.now(),
//                                InstantUtils.now()
//                        ),
//                        new CategoryEntity(
//                                IdUtils.generate(),
//                                "Playstation4",
//                                null,
//                                false,
//                                null,
//                                InstantUtils.now(),
//                                InstantUtils.now()
//                        )),
//                InstantUtils.now(),
//                InstantUtils.now());
//
//        // xbox category
//        final var xboxCategory = new CategoryEntity(
//                IdUtils.generate(),
//                "Xbox",
//                "video games",
//                false,
//                Set.of(
//                        new CategoryEntity(
//                                IdUtils.generate(),
//                                "XboxOne",
//                                null,
//                                false,
//                                null,
//                                InstantUtils.now(),
//                                InstantUtils.now()
//                        ),
//                        new CategoryEntity(
//                                IdUtils.generate(),
//                                "XboxSeries",
//                                null,
//                                false,
//                                null,
//                                InstantUtils.now(),
//                                InstantUtils.now()
//                        )),
//                InstantUtils.now(),
//                InstantUtils.now());

//        ab.getSubCategories().addAll(Set.of(playCategory, xboxCategory));
        this.categoryRepository.save(ab);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/sub/{id}")
    public ResponseEntity<?> subCategory(@PathVariable String id) {
        Set<CategoryEntity> sub = new HashSet<>();
//        sub.add(new CategoryEntity(
//                IdUtils.generate(),
//                "Controles",
//                "controle de video games",
//                Set.of(
//                        new CategoryEntity(
//                                IdUtils.generate(),
//                                "ControlePlaystation4",
//                                null,
//                                null,
//                                InstantUtils.now(),
//                                InstantUtils.now()
//                        )),
//                InstantUtils.now(),
//                InstantUtils.now()));

        var ab = this.categoryRepository.findById(id).get();
        ab.getSubCategories().addAll(sub);
        this.categoryRepository.save(ab);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Transactional
    @GetMapping
    public ResponseEntity<?> getCategoriesWithSubCategories() {
        final var a = new CategoryEntity(
                null,
                null,
                null,
                true,
                null,
                null,
                null
        );
        final var ar = categoryRepository.findAll(Example.of(a),
                        PageRequest.of(0, 2, Sort.by("createdAt").ascending()))
                .stream().map(CategoryGetDTO::from);
        return new ResponseEntity<>(ar, HttpStatus.OK);
    }

    @GetMapping("{name}")
    public ResponseEntity<?> getCategoryWithSubCategories(@PathVariable String name) {
        final var aResult = categoryRepository.findCategoryByName(name).map(CategoryGetDTO::from);

        return aResult.map(categoryGetDTO -> new ResponseEntity<>(categoryGetDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/sub/{name}")
    public ResponseEntity<?> getSubCategoryToFather(@PathVariable String name) {
        final var result = categoryRepository.findPathToCategory(name);
        if (!result.isEmpty()) {
            CategoryEntity category = result.get(result.size() - 1);

        return new ResponseEntity<>(CategoryGetDTO.from(category), HttpStatus.OK);
        }

        return null;
    }
}
