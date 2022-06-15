package com.example.demo.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Ulti.AppConstants;
import com.example.demo.payload.request.CategoryRequest;
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.security.service.UserDetailsImpl;
import com.example.demo.service.IServices.ICategoryService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
	private ICategoryService categoryService;

    @GetMapping()
	public PagedResponse<?> getAllCategories(
			@RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
			@RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
		return categoryService.getAllCategories(page, size);
	}
    @PostMapping
	@SecurityRequirement(name = "peniiz")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
	public ResponseEntity<?> addCategory(@Valid @RequestBody CategoryRequest category) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (UserDetailsImpl) auth.getPrincipal();
		return categoryService.addCategory(category, currentUser);
	}

    @GetMapping("/{id}")
	public ResponseEntity<?> getCategory(@PathVariable(name = "id") Long id) {
		return categoryService.getCategory(id);
	}

    @PutMapping("/{id}")
	@SecurityRequirement(name = "peniiz")
	@PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
	public ResponseEntity<?> updateCategory(@PathVariable(name = "id") Long id,
			@Valid @RequestBody CategoryRequest category) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (UserDetailsImpl) auth.getPrincipal();
		return categoryService.updateCategory(id, category, currentUser);
	}

    @DeleteMapping("/{id}")
	@SecurityRequirement(name = "peniiz")
	@PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
	public ResponseEntity<?> deleteCategory(@PathVariable(name = "id") Long id) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (UserDetailsImpl) auth.getPrincipal();
		return categoryService.deleteCategory(id, currentUser);
	}

}
