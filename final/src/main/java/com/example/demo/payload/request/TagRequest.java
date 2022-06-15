package com.example.demo.payload.request;

import javax.validation.constraints.NotBlank;

public class TagRequest {
    
    @NotBlank
    private String name;

    public TagRequest(){}
    public TagRequest(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
}
