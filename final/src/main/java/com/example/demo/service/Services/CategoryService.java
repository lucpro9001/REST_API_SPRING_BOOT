package com.example.demo.service.Services;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.Ulti.AppConstants;
import com.example.demo.Ulti.AppUtils;
import com.example.demo.models.Category;
import com.example.demo.payload.request.CategoryRequest;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.security.service.UserDetailsImpl;
import com.example.demo.service.IServices.ICategoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Service
public class CategoryService implements ICategoryService {

    @Autowired
	private CategoryRepository categoryRepository;

    @Override
    public PagedResponse<Category> getAllCategories(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, AppConstants.CREATED_AT);

		Page<Category> categories = categoryRepository.findAll(pageable);

		List<Category> content = categories.getNumberOfElements() == 0 ? Collections.emptyList() : categories.getContent();

		return new PagedResponse<>(content, categories.getNumber(), categories.getSize(), categories.getTotalElements(),
				categories.getTotalPages(), categories.isLast());
    }

    @Override
    public ResponseEntity<?> getCategory(Long id) {
        var query = categoryRepository.findById(id);
        if(query.isEmpty()) 
            return new ResponseEntity<>(
                new MessageResponse("Error: Category id is not found"), 
                HttpStatus.NOT_FOUND);
        var category = query.get();
		return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Category> addCategory(CategoryRequest category, UserDetailsImpl currentUser) {
        Category cate = new Category();
        cate.setName(category.getName());
        cate.setCreatedBy(currentUser.getId());
        Category newCategory = categoryRepository.save(cate);
		return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> updateCategory(Long id, CategoryRequest newCategory, UserDetailsImpl currentUser) {
        var qCategory = categoryRepository.findById(id);
        if(qCategory.isEmpty()) 
            return new ResponseEntity<>(
                new MessageResponse("Error: Category id is not found"), 
                HttpStatus.NOT_FOUND);
        var category = qCategory.get();
		if (category.getCreatedBy().equals(currentUser.getId()) 
            || AppUtils.isAdmin(currentUser)) {
			category.setName(newCategory.getName());
            category.setUpdatedBy(currentUser.getId());
            category.setUpdatedAt(Instant.now());
			Category updatedCategory = categoryRepository.save(category);
			return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
		}
        return new ResponseEntity<>(
            new MessageResponse("Error: You have no permission on this resource!"), 
            HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<?> deleteCategory(Long id, UserDetailsImpl currentUser) {
        var qCategory = categoryRepository.findById(id);
        if(qCategory.isEmpty()) 
            return new ResponseEntity<>(
                new MessageResponse("Error: Category id is not found"), 
                HttpStatus.NOT_FOUND);
        var category = qCategory.get();
		if (category.getCreatedBy().equals(currentUser.getId()) 
            || AppUtils.isAdmin(currentUser)) {
			categoryRepository.deleteById(id);
			return new ResponseEntity<>(new MessageResponse("Delete album successfully!"), HttpStatus.OK);
		}
        return new ResponseEntity<>(
            new MessageResponse("Error: You have no permission on this resource!"), 
            HttpStatus.FORBIDDEN);
    }
    
}
