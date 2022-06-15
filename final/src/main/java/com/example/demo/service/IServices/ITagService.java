package com.example.demo.service.IServices;

import org.springframework.http.ResponseEntity;

import com.example.demo.models.Tag;
import com.example.demo.payload.request.TagRequest;
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.security.service.UserDetailsImpl;

public interface ITagService {

	PagedResponse<Tag> getAllTags(int page, int size);

	ResponseEntity<?> getTag(Long id);

	ResponseEntity<?> addTag(TagRequest tag, UserDetailsImpl currentUser);

	ResponseEntity<?> updateTag(Long id, TagRequest newTag, UserDetailsImpl currentUser);

	ResponseEntity<?> deleteTag(Long id, UserDetailsImpl currentUser);

}
