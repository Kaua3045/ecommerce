package com.kaua.ecommerce.infrastructure.api.controllers;

import com.kaua.ecommerce.domain.utils.IdUtils;
import com.kaua.ecommerce.domain.utils.InstantUtils;
import com.kaua.ecommerce.infrastructure.category.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final CategoryElasticRepository categoryElasticRepository;

    public CategoryController(CategoryRepository categoryRepository, CategoryElasticRepository categoryElasticRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryElasticRepository = categoryElasticRepository;
    }

    @PostMapping
    public ResponseEntity<?> createCategory() {
        final var id = categoryRepository.save(new CategoryEntity(
                IdUtils.generate(),
                "Console",
                "Video games",
                null,
                new ArrayList<>(),
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

    @PostMapping("/sub/{id}")
    public ResponseEntity<?> subCategory(@PathVariable String id) {
        // principal category (console)
        var ab = this.categoryRepository.findById(id).get();

//        ab.getSubCategories().add(new CategoryEntity(
//                IdUtils.generate(),
//                "Blusas",
//                null,
//                false,
//                null,
//                InstantUtils.now(),
//                InstantUtils.now()
//        ));

        // playstation category
//        final var playCategory = new CategoryEntity(
//                IdUtils.generate(),
//                "Playstation",
//                "video games",
//                ab,
//                new ArrayList<>(),
//                InstantUtils.now(),
//                InstantUtils.now());
//
//        final var aPlay4Id = this.categoryRepository.save(new CategoryEntity(
//                IdUtils.generate(),
//                "Playstation4",
//                null,
//                playCategory,
//                new ArrayList<>(),
//                InstantUtils.now(),
//                InstantUtils.now()
//        ));
//        final var aPlay5Id = this.categoryRepository.save(
//                new CategoryEntity(
//                        IdUtils.generate(),
//                        "Playstation5",
//                        null,
//                        playCategory,
//                        new ArrayList<>(),
//                        InstantUtils.now(),
//                        InstantUtils.now()
//                ));
//
//        playCategory.setSubcategories(List.of(aPlay4Id, aPlay5Id));

        // xbox category
        final var xboxCategory = this.categoryRepository.save(new CategoryEntity(
                        IdUtils.generate(),
                        "Xbox",
                        "video games",
                        ab,
                        new ArrayList<>(),
                        InstantUtils.now(),
                        InstantUtils.now())
        );

        final var aXboxOneId = new CategoryEntity(
                IdUtils.generate(),
                "XboxOne",
                null,
                xboxCategory,
                new ArrayList<>(),
                InstantUtils.now(),
                InstantUtils.now()
        );
        final var aXboxSeriesId = new CategoryEntity(
                IdUtils.generate(),
                "XboxSeries",
                null,
                xboxCategory,
                new ArrayList<>(),
                InstantUtils.now(),
                InstantUtils.now()
        );

        xboxCategory.setSubcategories(List.of(aXboxOneId, aXboxSeriesId));

        this.categoryRepository.saveAll(Set.of(
                xboxCategory
        ));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("sync")
    public void sync() {
        List<CategoryEntity> jpaCategories = categoryRepository.findAll();
        List<CategoryEntityElastic> elasticsearchCategories = jpaCategories.stream()
                .map(category -> {
                    final var categoryElastic = new CategoryEntityElastic();
                    categoryElastic.setId(category.getId());
                    categoryElastic.setName(category.getName());
                    categoryElastic.setDescription(category.getDescription());
                    categoryElastic.setParentId(category.getParent() != null ? category.getParent().getId() : null);
                    categoryElastic.setCreatedAt(category.getCreatedAt());
                    return categoryElastic;
                })
                .toList();

        categoryElasticRepository.saveAll(elasticsearchCategories);
    }

//    public CategoriesResponse getHierarchicalCategories() {
//        List<CategoryEntityElastic> topLevelCategories = categoryElasticRepository.findByParentCategoryIdIsNull();
//
//        CategoriesResponse response = new CategoriesResponse();
//        response.setCategories(buildHierarchy(topLevelCategories));
//        return response;
//    }
//
//    private List<CategoryGetDTO> buildHierarchy(List<CategoryEntityElastic> categories) {
//        return categories.stream()
//                .map(category -> {
//                    category.setSubCategories(buildHierarchy(
//                            categoryElasticRepository.findByParentCategoryId(category.getId())));
//                    return category;
//                })
//                .toList();
//    }

    @Transactional
    @GetMapping
    public ResponseEntity<?> getCategoriesWithSubCategories(
            @RequestParam(value = "page", defaultValue = "0") String page
    ) {
        final var ar = categoryRepository.findByParentIsNull(
                        PageRequest.of(Integer.parseInt(page), 1, Sort.by("createdAt").ascending()))
                .stream().map(CategoryGetDTO::from);
        return new ResponseEntity<>(ar, HttpStatus.OK);
    }

    @GetMapping("{name}")
    public ResponseEntity<?> getCategoryWithSubCategories(@PathVariable String name) {
        final var aResult = categoryElasticRepository.findByName(name);

        return aResult.map(categoryGetDTO -> new ResponseEntity<>(categoryGetDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
//        return null;
    }

    @GetMapping("/sub/{name}")
    public ResponseEntity<?> getSubCategoryToFather(@PathVariable String name) {
//        final var result = categoryRepository.findPathToCategory(name);
//        if (!result.isEmpty()) {
//            CategoryEntity category = result.get(result.size() - 1);
//
//        return new ResponseEntity<>(CategoryGetDTO.from(category), HttpStatus.OK);
//        }

        return null;
    }
}
