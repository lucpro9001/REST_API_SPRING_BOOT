package com.example.demo.service.Services;

import com.example.demo.Ulti.AppUtils;
import com.example.demo.models.Post;
import com.example.demo.models.PostRequest;
import com.example.demo.models.Tag;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.payload.response.PostResponse;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.TagRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.service.UserDetailsImpl;
import com.example.demo.service.IServices.IPostService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.time.Instant;
import java.util.ArrayList;

import static com.example.demo.Ulti.AppConstants.CREATED_AT;

@Service
public class PostService implements IPostService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public PagedResponse<Post> getAllPosts(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, CREATED_AT);

        Page<Post> posts = postRepository.findAll(pageable);

        List<Post> content = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

        return new PagedResponse<>(content, posts.getNumber(), posts.getSize(), posts.getTotalElements(),
                posts.getTotalPages(), posts.isLast());
    }

    @Override
    public PagedResponse<Post> getPostsByCreatedBy(String username, int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);
        var query = userRepository.findByUsername(username);
        if (query.isEmpty())
            return null;
        var user = query.get();
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, CREATED_AT);
        Page<Post> posts = postRepository.findByCreatedBy(user.getId(), pageable);

        List<Post> content = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

        return new PagedResponse<>(content, posts.getNumber(), posts.getSize(), posts.getTotalElements(),
                posts.getTotalPages(), posts.isLast());
    }

    @Override
    public PagedResponse<Post> getPostsByCategory(Long id, int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);
        var query = categoryRepository.findById(id);
        if (query.isEmpty())
            return null;
        var category = query.get();

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, CREATED_AT);
        Page<Post> posts = postRepository.findByCategoryId(category.getId(), pageable);

        List<Post> content = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();

        return new PagedResponse<>(content, posts.getNumber(), posts.getSize(), posts.getTotalElements(),
                posts.getTotalPages(), posts.isLast());
    }

    @Override
    public ResponseEntity<?> updatePost(Long id, PostRequest newPostRequest, UserDetailsImpl currentUser) {
        var queryPost = postRepository.findById(id);
        if (queryPost.isEmpty())
        return new ResponseEntity<>(
            new MessageResponse("Error: Post id is not found"), 
            HttpStatus.NOT_FOUND);
        var queryCate = categoryRepository.findById(newPostRequest.getCategoryId());
        if (queryCate.isEmpty())
            return new ResponseEntity<>(
                new MessageResponse("Error: Category id is not found"), 
                HttpStatus.NOT_FOUND);
        var post = queryPost.get();
        var category = queryCate.get();
        if (post.getUser().getId().equals(currentUser.getId())
                || AppUtils.isAdmin(currentUser)) {
            post.setTitle(newPostRequest.getTitle());
            post.setBody(newPostRequest.getBody());
            post.setCategory(category);
            post.setUpdatedAt(Instant.now());
            post.setUpdatedBy(currentUser.getId());
            var res = postRepository.save(post);
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(
            new MessageResponse("Error: You have no permission on this resource!"), 
            HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<?> deletePost(Long id, UserDetailsImpl currentUser) {
        var query = postRepository.findById(id);
        if (query.isEmpty())
        return new ResponseEntity<>(
            new MessageResponse("Error: Post id is not found"), 
            HttpStatus.NOT_FOUND);
        var post = query.get();
        if (post.getUser().getId().equals(currentUser.getId())
                || AppUtils.isAdmin(currentUser)) {
            postRepository.deleteById(id);
            return new ResponseEntity<>(new MessageResponse("Delete post successfully!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(
            new MessageResponse("Error: You have no permission on this resource!"), 
            HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<?> addPost(PostRequest postRequest, UserDetailsImpl currentUser) {
        var user = userRepository.findById(currentUser.getId()).get();
        var queryCate = categoryRepository.findById(postRequest.getCategoryId());
        if (queryCate.isEmpty())
            return new ResponseEntity<>(
                new MessageResponse("Error: Category id is not found"), 
                HttpStatus.NOT_FOUND);
        var category = queryCate.get();
        List<Tag> tags = new ArrayList<>(postRequest.getTags().size());

        for (String name : postRequest.getTags()) {
            Tag tag = tagRepository.findByName(name);
            if (tag == null) {
                tag = new Tag(name);
                tag.setCreatedBy(user.getId());
                tagRepository.save(tag);
            }
            tags.add(tag);
        }

        Post post = new Post();
        post.setBody(postRequest.getBody());
        post.setTitle(postRequest.getTitle());
        post.setCategory(category);
        post.setUser(user);
        post.setTags(tags);
        post.setCreatedBy(currentUser.getId());
        Post newPost = postRepository.save(post);

        PostResponse postResponse = new PostResponse();

        postResponse.setTitle(newPost.getTitle());
        postResponse.setBody(newPost.getBody());
        postResponse.setCategory(newPost.getCategory().getName());

        List<String> tagNames = new ArrayList<>(newPost.getTags().size());

        for (Tag tag : newPost.getTags()) {
            tagNames.add(tag.getName());
        }

        postResponse.setTags(tagNames);

        return new ResponseEntity<>(postResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> getPost(Long id) {
        var query = postRepository.findById(id);
        if(query.isEmpty()) 
            return new ResponseEntity<>(
                new MessageResponse("Error: Post id is not found"), 
                HttpStatus.NOT_FOUND);
        return new ResponseEntity< >(query.get(), HttpStatus.OK);
    }

}
