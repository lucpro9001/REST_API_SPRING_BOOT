package com.example.demo.service.IServices;

import com.example.demo.payload.request.CategoryRequest;
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.security.service.UserDetailsImpl;

import org.springframework.http.ResponseEntity;

public interface ICategoryService {

	PagedResponse<?> getAllCategories(int page, int size);

	ResponseEntity<?> getCategory(Long id);

	ResponseEntity<?> addCategory(CategoryRequest category, UserDetailsImpl currentUser);

	ResponseEntity<?> updateCategory(Long id, CategoryRequest newCategory, UserDetailsImpl currentUser);

	ResponseEntity<?> deleteCategory(Long id, UserDetailsImpl currentUser);

}
