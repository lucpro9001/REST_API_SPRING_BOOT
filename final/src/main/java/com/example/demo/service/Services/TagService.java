package com.example.demo.service.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import com.example.demo.Ulti.AppUtils;
import com.example.demo.models.Tag;
import com.example.demo.payload.request.TagRequest;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.repository.TagRepository;
import com.example.demo.security.service.UserDetailsImpl;
import com.example.demo.service.IServices.ITagService;

@Service
public class TagService implements ITagService {
    @Autowired
    private TagRepository tagRepository;

    @Override
    public PagedResponse<Tag> getAllTags(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        Page<Tag> tags = tagRepository.findAll(pageable);

        List<Tag> content = tags.getNumberOfElements() == 0 ? Collections.emptyList() : tags.getContent();

        return new PagedResponse<>(content, tags.getNumber(), tags.getSize(), tags.getTotalElements(),
                tags.getTotalPages(), tags.isLast());
    }

    @Override
    public ResponseEntity<?> getTag(Long id) {
        var tag = tagRepository.findById(id);
        if (tag.isEmpty())
            return new ResponseEntity<>(
                    new MessageResponse("Error: Tag id is not found"),
                    HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(tag.get(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> addTag(TagRequest tag, UserDetailsImpl currentUser) {
        Tag t = new Tag();
        t.setCreatedBy(currentUser.getId());
        t.setName(tag.getName());
        var res = tagRepository.save(t);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> updateTag(Long id, TagRequest newTag, UserDetailsImpl currentUser) {
        var tag = tagRepository.findById(id);
        if (tag.isEmpty())
            return new ResponseEntity<>(
                    new MessageResponse("Error: Tag id is not found"),
                    HttpStatus.NOT_FOUND);
        var t = tag.get();
        if (t.getCreatedBy().equals(currentUser.getId())
            || AppUtils.isAdmin(currentUser) 
            || AppUtils.isModerator(currentUser)) {
            t.setName(newTag.getName());
            t.setUpdatedAt(Instant.now());
            t.setUpdatedBy(currentUser.getId());
            var res = tagRepository.save(t);
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(
                new MessageResponse("Error: You have no permission on this resource!"),
                HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<?> deleteTag(Long id, UserDetailsImpl currentUser) {
        var tag = tagRepository.findById(id);
        if (tag.isEmpty())
            return new ResponseEntity<>(
                    new MessageResponse("Error: Tag id is not found"),
                    HttpStatus.NOT_FOUND);
        var t = tag.get();
        if (t.getCreatedBy().equals(currentUser.getId())
            || AppUtils.isAdmin(currentUser) 
            || AppUtils.isModerator(currentUser)) {
            tagRepository.deleteById(id);
            return new ResponseEntity<>(new MessageResponse("Delete tag successfully!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(
                new MessageResponse("Error: You have no permission on this resource!"),
                HttpStatus.FORBIDDEN);
    }

}
