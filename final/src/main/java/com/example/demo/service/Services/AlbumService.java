package com.example.demo.service.Services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.Ulti.AppConstants;
import com.example.demo.Ulti.AppUtils;
import com.example.demo.models.Album;
import com.example.demo.payload.request.AlbumRequest;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.repository.AlbumRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.service.UserDetailsImpl;
import com.example.demo.service.IServices.IAlbumService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
public class AlbumService implements IAlbumService {

    @Autowired
	private AlbumRepository albumRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ModelMapper modelMapper;

    @Override
    public PagedResponse<?> getAllAlbums(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, AppConstants.CREATED_AT);

		Page<Album> albums = albumRepository.findAll(pageable);

		List<Album> content = albums.getNumberOfElements() == 0 ? Collections.emptyList() : albums.getContent();

		return new PagedResponse<>(content, albums.getNumber(), albums.getSize(), albums.getTotalElements(), albums.getTotalPages(),
				albums.isLast());
    }

    @Override
    public ResponseEntity<?> addAlbum(AlbumRequest albumRequest, UserDetailsImpl currentUser) {
        var user = userRepository.findById(currentUser.getId()).get();
		Album album = new Album();
		modelMapper.map(albumRequest, album);
		album.setUser(user);
        album.setCreatedBy(user.getId());
		Album newAlbum = albumRepository.save(album);
		return new ResponseEntity<>(newAlbum, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> getAlbum(Long id) {
        var query = albumRepository.findById(id);
        if(query.isEmpty()) 
            return new ResponseEntity<>(
                new MessageResponse("Error: Album id is not found"), 
                HttpStatus.NOT_FOUND);
        var album = query.get();
		return new ResponseEntity<>(album, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> updateAlbum(Long id, AlbumRequest newAlbum, UserDetailsImpl currentUser) {
        var query = albumRepository.findById(id);
        if(query.isEmpty()) 
            return new ResponseEntity<>(
                new MessageResponse("Error: Album id is not found"), 
                HttpStatus.NOT_FOUND);
        var album = query.get();
		var queryUser = userRepository.findById(currentUser.getId());
        if(queryUser.isEmpty()) 
            return new ResponseEntity<>(
                new MessageResponse("Error: User is not found"), 
                HttpStatus.NOT_FOUND);
        var user = queryUser.get();
		if (album.getUser().getId().equals(user.getId()) 
            || AppUtils.isAdmin(currentUser)) {
			album.setTitle(newAlbum.getTitle());
            album.setUpdatedAt(Instant.now());
            album.setUpdatedBy(currentUser.getId());
			Album updatedAlbum = albumRepository.save(album);

			return new ResponseEntity<>(updatedAlbum, HttpStatus.OK);
		}
        return new ResponseEntity<>(
            new MessageResponse("Error: You have no permission on this resource!"), 
            HttpStatus.FORBIDDEN);

    }

    @Override
    public ResponseEntity<?> deleteAlbum(Long id, UserDetailsImpl currentUser) {
        var query = albumRepository.findById(id);
        if(query.isEmpty()) 
        return new ResponseEntity<>(
            new MessageResponse("Error: Album id is not found!"), 
            HttpStatus.NOT_FOUND);
        var album = query.get();
		var queryUser = userRepository.findById(currentUser.getId());
        if(queryUser.isEmpty()) 
            return new ResponseEntity<>(
                new MessageResponse("Error: User is not found"), 
                HttpStatus.NOT_FOUND);
        var user = queryUser.get();
		if (album.getUser().getId().equals(user.getId()) 
            || AppUtils.isAdmin(currentUser)) {
			albumRepository.deleteById(id);
			return new ResponseEntity<>(new MessageResponse("Delete album successfully!"), HttpStatus.OK);
		}
        return new ResponseEntity<>(
            new MessageResponse("Error: You have no permission on this resource!"), 
            HttpStatus.FORBIDDEN);
    }

    @Override
    public PagedResponse<Album> getUserAlbums(String username, int page, int size) {
        var queryUser = userRepository.findByUsername(username);
        if(queryUser.isEmpty()) 
            return null;
        var user = queryUser.get();

		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, AppConstants.CREATED_AT);

		Page<Album> albums = albumRepository.findByCreatedBy(user.getId(), pageable);

		List<Album> content = albums.getNumberOfElements() > 0 ? albums.getContent() : Collections.emptyList();

		return new PagedResponse<>(content, albums.getNumber(), albums.getSize(), albums.getTotalElements(), albums.getTotalPages(), albums.isLast());
	}
}

