package com.example.demo.service.Services;

import com.example.demo.models.Photo;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.payload.request.PhotoRequest;
import com.example.demo.payload.request.PhotoUpdateRequest;
import com.example.demo.payload.response.PhotoResponse;
import com.example.demo.repository.AlbumRepository;
import com.example.demo.repository.PhotoRepository;
import com.example.demo.security.service.UserDetailsImpl;
import com.example.demo.service.IServices.IPhotoService;
import com.example.demo.service.Services.PhotoService;
import com.example.demo.Ulti.AppConstants;
import com.example.demo.Ulti.AppUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.example.demo.Ulti.AppConstants.CREATED_AT;

@Service
public class PhotoService implements IPhotoService {
    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Override
    public PagedResponse<PhotoResponse> getAllPhotos(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, CREATED_AT);
        Page<Photo> photos = photoRepository.findAll(pageable);

        List<PhotoResponse> photoResponses = new ArrayList<>(photos.getContent().size());
        for (Photo photo : photos.getContent()) {
            photoResponses.add(new PhotoResponse(photo.getId(),
                    photo.getTitle(), photo.getUrl(),
                    photo.getAlbum().getId()));
        }

        if (photos.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), photos.getNumber(), photos.getSize(),
                    photos.getTotalElements(), photos.getTotalPages(), photos.isLast());
        }
        return new PagedResponse<>(photoResponses, photos.getNumber(), photos.getSize(), photos.getTotalElements(),
                photos.getTotalPages(), photos.isLast());
    }

    @Override
    public ResponseEntity<?> getPhoto(Long id) {
        var query = photoRepository.findById(id);
        if (query.isEmpty())
            return new ResponseEntity<>(
                    new MessageResponse("Error: Photo id is not found"),
                    HttpStatus.NOT_FOUND);
        var photo = query.get();
        var res = new PhotoResponse(
                photo.getId(), photo.getTitle(), photo.getUrl(),
                photo.getAlbum().getId());
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> addPhoto(PhotoRequest photoRequest, UserDetailsImpl currentUser) {
        var qAlbum = albumRepository.findById(photoRequest.getAlbumId());
        if (qAlbum.isEmpty())
            return new ResponseEntity<>(
                    new MessageResponse("Error: Album id is not found"),
                    HttpStatus.NOT_FOUND);
        var album = qAlbum.get();
        var file = photoRequest.getImage();
        var extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") - 1);
        var fileName = UUID.randomUUID().toString() + extension;
        String uploadDir = new java.io.File("src\\main\\resources\\static\\images").getAbsolutePath();
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        var photo = new Photo();
        photo.setAlbum(album);
        photo.setCreatedBy(currentUser.getId());
        photo.setTitle(photoRequest.getTitle());
        var url = "/images/" + fileName;
        photo.setUrl(url);
        var res = photoRepository.save(photo);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> deletePhoto(Long id, UserDetailsImpl currentUser) {
        var qPhoto = photoRepository.findById(id);
        if (qPhoto.isEmpty())
            return new ResponseEntity<>(
                    new MessageResponse("Error: Photo id is not found"),
                    HttpStatus.NOT_FOUND);
        var photo = qPhoto.get();
        if (photo.getAlbum().getUser().getId().equals(currentUser.getId())
                || AppUtils.isAdmin(currentUser)) {
            photoRepository.deleteById(id);
            return new ResponseEntity<>(new MessageResponse("Delete photo successfully!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(
                new MessageResponse("Error: You have no permission on this resource!"),
                HttpStatus.FORBIDDEN);
    }

    @Override
    public PagedResponse<?> getAllPhotosByAlbum(Long albumId, int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, AppConstants.CREATED_AT);

        Page<Photo> photos = photoRepository.findByAlbumId(albumId, pageable);

        List<PhotoResponse> photoResponses = new ArrayList<>(photos.getContent().size());
        for (Photo photo : photos.getContent()) {
            photoResponses.add(
                    new PhotoResponse(photo.getId(), photo.getTitle(),
                            photo.getUrl(), photo.getAlbum().getId()));
        }

        return new PagedResponse<>(photoResponses, photos.getNumber(), photos.getSize(), photos.getTotalElements(),
                photos.getTotalPages(), photos.isLast());
    }

    @Override
    public ResponseEntity<?> updatePhoto(Long id, PhotoUpdateRequest photoRequest, UserDetailsImpl currentUser) {
        var qAlbum = albumRepository.findById(photoRequest.getAlbumId());
        if (qAlbum.isEmpty())
            return new ResponseEntity<>(
                    new MessageResponse("Error: Album id is not found"),
                    HttpStatus.NOT_FOUND);
        var album = qAlbum.get();
        var qPhoto = photoRepository.findById(id);
        if (qPhoto.isEmpty())
            return new ResponseEntity<>(
                    new MessageResponse("Error: Photo id is not found"),
                    HttpStatus.NOT_FOUND);
        var photo = qPhoto.get();
        if (photo.getAlbum().getUser().getId().equals(currentUser.getId())
                || AppUtils.isAdmin(currentUser)) {
            photo.setTitle(photoRequest.getTitle());
            photo.setUpdatedAt(Instant.now());
            photo.setUpdatedBy(currentUser.getId());
            photo.setAlbum(album);
            Photo updatedPhoto = photoRepository.save(photo);
            return new ResponseEntity<>(updatedPhoto, HttpStatus.OK);
        }
        return new ResponseEntity<>(
                new MessageResponse("Error: You have no permission on this resource!"),
                HttpStatus.FORBIDDEN);
    }

}
