package com.example.demo.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class PhotoUpdateRequest {
    
    @NotBlank
    private String title;
    @NotNull
    private Long albumId;
    public PhotoUpdateRequest(@NotBlank String title, @NotNull Long albumId) {
        this.title = title;
        this.albumId = albumId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Long getAlbumId() {
        return albumId;
    }
    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    
}
