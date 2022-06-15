package com.example.demo.service.IServices;

import org.springframework.http.ResponseEntity;

import com.example.demo.controllers.CommentRequest;
import com.example.demo.models.Comment;

import com.example.demo.payload.response.PagedResponse;
import com.example.demo.security.service.UserDetailsImpl;

public interface ICommentService {

	PagedResponse<Comment> getAllComments(Long postId, int page, int size);

	ResponseEntity<?> addComment(CommentRequest commentRequest, Long postId, UserDetailsImpl currentUser);

	ResponseEntity<?> getComment(Long postId, Long id);

	ResponseEntity<?> updateComment(Long postId, Long id, CommentRequest commentRequest, UserDetailsImpl currentUser);

	ResponseEntity<?> deleteComment(Long postId, Long id, UserDetailsImpl currentUser);

}
