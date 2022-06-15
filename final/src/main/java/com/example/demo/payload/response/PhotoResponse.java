package com.example.demo.payload.response;


import lombok.Data;

@Data
public class PhotoResponse {
	private Long id;
	private String title;
	private String url;
	private Long albumId;

	public PhotoResponse(Long id, String title, String url, Long albumId) {
		this.id = id;
		this.title = title;
		this.url = url;
		this.albumId = albumId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getAlbumId() {
		return albumId;
	}

	public void setAlbumId(Long albumId) {
		this.albumId = albumId;
	}

}