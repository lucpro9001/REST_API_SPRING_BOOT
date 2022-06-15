package com.example.demo.service.IServices;

import org.springframework.http.ResponseEntity;

import com.example.demo.payload.request.PhotoRequest;
import com.example.demo.payload.request.PhotoUpdateRequest;
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.security.service.UserDetailsImpl;

public interface IPhotoService {

	PagedResponse<?> getAllPhotos(int page, int size);

	ResponseEntity<?> getPhoto(Long id);

	ResponseEntity<?> addPhoto(PhotoRequest photoRequest, UserDetailsImpl currentUser);

	ResponseEntity<?> updatePhoto(Long id, PhotoUpdateRequest photoRequest, UserDetailsImpl currentUser);

	ResponseEntity<?> deletePhoto(Long id, UserDetailsImpl currentUser);

	PagedResponse<?> getAllPhotosByAlbum(Long albumId, int page, int size);

}
