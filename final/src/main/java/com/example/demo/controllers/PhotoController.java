package com.example.demo.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Ulti.AppConstants;
import com.example.demo.payload.request.PhotoRequest;
import com.example.demo.payload.request.PhotoUpdateRequest;
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.security.service.UserDetailsImpl;
import com.example.demo.service.IServices.IPhotoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {
    
    @Autowired
	private IPhotoService photoService;

    @GetMapping()
	public PagedResponse<?> getAllPhotos(
			@RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
			@RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
		return photoService.getAllPhotos(page, size);
	}
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("isAuthenticated()")
	@SecurityRequirement(name = "peniiz")
	public ResponseEntity<?> addPhoto(@Valid @ModelAttribute PhotoRequest request) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (UserDetailsImpl) auth.getPrincipal();
		return photoService.addPhoto(request, currentUser);
		
	}
	@GetMapping("/{id}")
	public ResponseEntity<?> getPhoto(@PathVariable(name = "id") Long id) {
		return photoService.getPhoto(id);
	}
    @PutMapping("/{id}")
	@SecurityRequirement(name = "peniiz")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> updatePhoto(@PathVariable(name = "id") Long id,
			@Valid @RequestBody PhotoUpdateRequest photoRequest) {
		var auth = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (UserDetailsImpl) auth.getPrincipal();
		return photoService.updatePhoto(id, photoRequest, currentUser);
	}
	@DeleteMapping("/{id}")
	@SecurityRequirement(name = "peniiz")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> deletePhoto(@PathVariable(name = "id") Long id) {
		var auth = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (UserDetailsImpl) auth.getPrincipal();
		return photoService.deletePhoto(id, currentUser);
	}
}
