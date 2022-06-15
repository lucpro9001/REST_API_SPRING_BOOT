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
import com.example.demo.models.Post;
import com.example.demo.models.PostRequest;
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.security.service.UserDetailsImpl;
import com.example.demo.service.IServices.IPostService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/posts")

public class PostController {
    @Autowired
	private IPostService postService;
    @GetMapping()
	public PagedResponse<Post> getAllPosts(
			@RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
			@RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
		return postService.getAllPosts(page, size);
	}

    @GetMapping("/category/{id}")
	public PagedResponse<Post> getPostsByCategory(
			@RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
			@RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size,
			@PathVariable(name = "id") Long id) {
		return postService.getPostsByCategory(id, page, size);
	}

    @PostMapping()
	@PreAuthorize("isAuthenticated()")
	@SecurityRequirement(name = "peniiz")
	public ResponseEntity<?> addPost(@Valid @RequestBody PostRequest postRequest) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (UserDetailsImpl) auth.getPrincipal();
		return postService.addPost(postRequest, currentUser);
	}

    @GetMapping("/{id}")
	public ResponseEntity<?> getPost(@PathVariable(name = "id") Long id) {
		return postService.getPost(id);
	}
    @PutMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	@SecurityRequirement(name = "peniiz")
	public ResponseEntity<?> updatePost(@PathVariable(name = "id") Long id,
			@Valid @RequestBody PostRequest newPostRequest) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (UserDetailsImpl) auth.getPrincipal();
		return postService.updatePost(id, newPostRequest, currentUser);
	}
    @DeleteMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	@SecurityRequirement(name = "peniiz")
	public ResponseEntity<?> deletePost(@PathVariable(name = "id") Long id) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (UserDetailsImpl) auth.getPrincipal();
		return postService.deletePost(id, currentUser);
	}
}
