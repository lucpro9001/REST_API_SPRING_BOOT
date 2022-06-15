package com.example.demo.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class TodoRequest {
    @NotBlank
    private String title;
    private Boolean completed;
    public TodoRequest(@NotBlank String title, Boolean completed) {
        this.title = title;
        this.completed = completed;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Boolean getCompleted() {
        return completed;
    }
    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }


}
