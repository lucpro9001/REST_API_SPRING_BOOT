package com.example.demo.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class AlbumRequest {
    @NotBlank
    private String title;

    public AlbumRequest() {

    }
    
    public AlbumRequest(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}
