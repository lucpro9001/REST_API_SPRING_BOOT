package com.example.demo.service.IServices;

import com.example.demo.payload.request.AlbumRequest;
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.security.service.UserDetailsImpl;

import org.springframework.http.ResponseEntity;

public interface IAlbumService {

	PagedResponse<?> getAllAlbums(int page, int size);

	ResponseEntity<?> addAlbum(AlbumRequest albumRequest, UserDetailsImpl currentUser);

	ResponseEntity<?> getAlbum(Long id);

	ResponseEntity<?> updateAlbum(Long id, AlbumRequest newAlbum, UserDetailsImpl currentUser);

	ResponseEntity<?> deleteAlbum(Long id, UserDetailsImpl currentUser);

	PagedResponse<?> getUserAlbums(String username, int page, int size);

}
