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
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.security.service.UserDetailsImpl;
import com.example.demo.service.IServices.ICommentService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {
    @Autowired
    private ICommentService commentService;

    @GetMapping()
    public PagedResponse<?> getAllComments(@PathVariable(name = "postId") Long postId,
            @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {

        return commentService.getAllComments(postId, page, size);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "peniiz")
    public ResponseEntity<?> addComment(
            @Valid @RequestBody CommentRequest commentRequest,
            @PathVariable(name = "postId") Long postId) {

        var auth = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (UserDetailsImpl) auth.getPrincipal();
        return commentService.addComment(commentRequest, postId, currentUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getComment(@PathVariable(name = "postId") Long postId,
            @PathVariable(name = "id") Long id) {
        return commentService.getComment(postId, id);
    }

    @PutMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "peniiz")
	public ResponseEntity<?> updateComment(
            @PathVariable(name = "postId") Long postId,
			@PathVariable(name = "id") Long id,
            @Valid @RequestBody CommentRequest commentRequest) {

        var auth = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (UserDetailsImpl) auth.getPrincipal();
		return commentService.updateComment(postId, id, commentRequest, currentUser);
	}

    @DeleteMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "peniiz")
	public ResponseEntity<?> deleteComment(
            @PathVariable(name = "postId") Long postId,
			@PathVariable(name = "id") Long id) {

        var auth = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (UserDetailsImpl) auth.getPrincipal();
		return commentService.deleteComment(postId, id, currentUser);
	}


}
