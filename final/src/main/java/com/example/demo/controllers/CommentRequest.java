package com.example.demo.controllers;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CommentRequest {
    @NotBlank
	@Size(min = 10, message = "Comment body must be minimum 10 characters")
	private String body;

    public CommentRequest(){}

	public CommentRequest(String body) {
		this.body = body;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
