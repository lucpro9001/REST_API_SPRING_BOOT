package com.example.demo.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class CategoryRequest {
    
    @NotBlank
    private String name;

    public CategoryRequest() {
    }

    public CategoryRequest(@NotBlank String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
