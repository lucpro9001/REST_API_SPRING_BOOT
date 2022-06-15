package com.example.demo.controllers;

import javax.validation.Valid;

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

import com.example.demo.Ulti.AppConstants;
import com.example.demo.Ulti.AppUtils;
import com.example.demo.payload.request.AlbumRequest;
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.security.service.UserDetailsImpl;
import com.example.demo.service.IServices.IAlbumService;
import com.example.demo.service.IServices.IPhotoService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {
    @Autowired
	private IAlbumService albumService;

	@Autowired
	private IPhotoService photoService;

    @GetMapping()
	public PagedResponse<?> getAllAlbums(
			@RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
			@RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
		AppUtils.validatePageNumberAndSize(page, size);
        return albumService.getAllAlbums(page, size);
	}
    @PostMapping()
	@PreAuthorize("isAuthenticated()")
	@SecurityRequirement(name = "peniiz")
	public ResponseEntity<?> addAlbum(@Valid @RequestBody AlbumRequest albumRequest) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (UserDetailsImpl) auth.getPrincipal();
		return albumService.addAlbum(albumRequest, currentUser);
	}

    @GetMapping("/{id}")
	public ResponseEntity<?> getAlbum(@PathVariable(name = "id") Long id) {
		return albumService.getAlbum(id);
	}

    @PutMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	@SecurityRequirement(name = "peniiz")
	public ResponseEntity<?> updateAlbum(
        @PathVariable(name = "id") Long id, 
        @Valid @RequestBody AlbumRequest newAlbum) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (UserDetailsImpl) auth.getPrincipal();
		return albumService.updateAlbum(id, newAlbum, currentUser);
	}

    @DeleteMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	@SecurityRequirement(name = "peniiz")
	public ResponseEntity<?> deleteAlbum(@PathVariable(name = "id") Long id) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (UserDetailsImpl) auth.getPrincipal();
		return albumService.deleteAlbum(id, currentUser);
	}
    @GetMapping("/{id}/photos")
	public ResponseEntity<PagedResponse<?>> getAllPhotosByAlbum(@PathVariable(name = "id") Long id,
			@RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
			@RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {

		PagedResponse<?> response = photoService.getAllPhotosByAlbum(id, page, size);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
