package com.example.demo.service.IServices;


import org.springframework.http.ResponseEntity;

import com.example.demo.models.Post;
import com.example.demo.models.PostRequest;
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.security.service.UserDetailsImpl;

public interface IPostService {

	PagedResponse<Post> getAllPosts(int page, int size);

	PagedResponse<Post> getPostsByCreatedBy(String username, int page, int size);

	PagedResponse<Post> getPostsByCategory(Long id, int page, int size);

	ResponseEntity<?> updatePost(Long id, PostRequest newPostRequest, UserDetailsImpl currentUser);

	ResponseEntity<?> deletePost(Long id, UserDetailsImpl currentUser);

	ResponseEntity<?> addPost(PostRequest postRequest, UserDetailsImpl currentUser);

	ResponseEntity<?> getPost(Long id);

}
