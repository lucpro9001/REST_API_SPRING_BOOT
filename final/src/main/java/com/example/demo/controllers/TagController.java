package com.example.demo.controllers;

import javax.validation.Valid;

import com.example.demo.Ulti.AppConstants;
import com.example.demo.models.Tag;
import com.example.demo.payload.request.TagRequest;
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.security.service.UserDetailsImpl;
import com.example.demo.service.IServices.ITagService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/tags")
public class TagController {
	@Autowired
	private ITagService tagService;

	@GetMapping()
	public ResponseEntity<?> getAllTags(
			@RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
			@RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {

		PagedResponse<Tag> response = tagService.getAllTags(page, size);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getTag(@PathVariable(name = "id") Long id) {
		return tagService.getTag(id);
	}

	@PostMapping()
	@PreAuthorize("isAuthenticated()")
	@SecurityRequirement(name = "peniiz")
	public ResponseEntity<?> addTag(@Valid @RequestBody TagRequest tag) {
		var auth = SecurityContextHolder.getContext().getAuthentication();
		var currentUser = (UserDetailsImpl) auth.getPrincipal();
		return tagService.addTag(tag, currentUser);
	}

	@PutMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	@SecurityRequirement(name = "peniiz")
	public ResponseEntity<?> updateTag(@PathVariable(name = "id") Long id, @Valid @RequestBody TagRequest tag) {
		var auth = SecurityContextHolder.getContext().getAuthentication();
		var currentUser = (UserDetailsImpl) auth.getPrincipal();	
		return tagService.updateTag(id, tag, currentUser);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	@SecurityRequirement(name = "peniiz")
	public ResponseEntity<?> deleteTag(@PathVariable(name = "id") Long id) {
		var auth = SecurityContextHolder.getContext().getAuthentication();
		var currentUser = (UserDetailsImpl) auth.getPrincipal();
		return tagService.deleteTag(id, currentUser);
	}
}
