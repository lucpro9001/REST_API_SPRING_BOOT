package com.example.demo.service.Services;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.Ulti.AppUtils;
import com.example.demo.controllers.CommentRequest;
import com.example.demo.models.Comment;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.service.UserDetailsImpl;
import com.example.demo.service.IServices.ICommentService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
@Service
public class CommentService implements ICommentService {

    @Autowired
	private CommentRepository commentRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private UserRepository userRepository;

    @Override
    public PagedResponse<Comment> getAllComments(Long postId, int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);
		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

		Page<Comment> comments = commentRepository.findByPostId(postId, pageable);

		return new PagedResponse<>(comments.getContent(), comments.getNumber(), comments.getSize(),
				comments.getTotalElements(), comments.getTotalPages(), comments.isLast());
    }

    @Override
    public ResponseEntity<?> addComment(CommentRequest commentRequest, Long postId, UserDetailsImpl currentUser) {
        var query = postRepository.findById(postId);
        if(query.isEmpty()) 
            return new ResponseEntity<>(
                new MessageResponse("Error: Post id is not found"), 
                HttpStatus.NOT_FOUND);
        var post = query.get();
        var user = userRepository.findById(currentUser.getId()).get();
		Comment comment = new Comment(commentRequest.getBody());
		comment.setUser(user);
		comment.setPost(post);
		comment.setName(currentUser.getUsername());
		comment.setEmail(currentUser.getEmail());
        comment.setCreatedBy(user.getId());
		var res = commentRepository.save(comment);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> getComment(Long postId, Long id) {
        var query = postRepository.findById(postId);
        if(query.isEmpty()) 
            return new ResponseEntity<>(
                new MessageResponse("Error: Post id is not found"), 
                HttpStatus.NOT_FOUND);
        var post = query.get();
		var queryComment = commentRepository.findById(id);
        if(queryComment.isEmpty()) 
            return new ResponseEntity<>(
                new MessageResponse("Error: Comment id is not found"), 
                HttpStatus.NOT_FOUND);
        var comment = queryComment.get();
		if (comment.getPost().getId().equals(post.getId())) {
			return new ResponseEntity<>(comment, HttpStatus.OK);
		}
        return new ResponseEntity<>(new MessageResponse("Comment does not belong to post!"), HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> updateComment(Long postId, Long id, CommentRequest commentRequest,
            UserDetailsImpl currentUser) {
        var query = postRepository.findById(postId);
        if(query.isEmpty()) 
            return new ResponseEntity<>(
                new MessageResponse("Error: Post id is not found"), 
                HttpStatus.NOT_FOUND);
        var post = query.get();
		var queryComment = commentRepository.findById(id);
        if(queryComment.isEmpty()) 
            return new ResponseEntity<>(
                new MessageResponse("Error: Comment id is not found"), 
                HttpStatus.NOT_FOUND);
        var comment = queryComment.get();

		if (!comment.getPost().getId().equals(post.getId())) {
			return new ResponseEntity<>(new MessageResponse("Comment does not belong to post!"), HttpStatus.BAD_REQUEST);
		}

		if (comment.getUser().getId().equals(currentUser.getId())
				|| AppUtils.isAdmin(currentUser)) {
			comment.setBody(commentRequest.getBody());
            comment.setUpdatedBy(currentUser.getId());
            comment.setUpdatedAt(Instant.now());
			return new ResponseEntity<>(comment, HttpStatus.OK);
		}

        return new ResponseEntity<>(
            new MessageResponse("Error: You have no permission on this resource!"), 
            HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<?> deleteComment(Long postId, Long id, UserDetailsImpl currentUser) {
        var query = postRepository.findById(postId);
        if(query.isEmpty()) 
            return new ResponseEntity<>(
                new MessageResponse("Error: Post id is not found"), 
                HttpStatus.NOT_FOUND);
        var post = query.get();
		var queryComment = commentRepository.findById(id);
        if(queryComment.isEmpty()) 
            return new ResponseEntity<>(
                new MessageResponse("Error: Comment id is not found"), 
                HttpStatus.NOT_FOUND);
        var comment = queryComment.get();


		if (!comment.getPost().getId().equals(post.getId())) {
			return new ResponseEntity<>(new MessageResponse("Comment does not belong to post!"), HttpStatus.BAD_REQUEST);
		}

		if (comment.getUser().getId().equals(currentUser.getId())
				|| AppUtils.isAdmin(currentUser)) {
			commentRepository.deleteById(comment.getId());
			return new ResponseEntity<>(new MessageResponse("Delete comment successfully!"), HttpStatus.OK);
		}
        return new ResponseEntity<>(
            new MessageResponse("Error: You have no permission on this resource!"), 
            HttpStatus.FORBIDDEN);
    }
    
}
