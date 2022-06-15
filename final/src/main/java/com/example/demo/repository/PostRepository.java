package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.example.demo.models.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	
	Page<Post> findByCreatedBy(Long userId, Pageable pageable);

	Long countByCreatedBy(Long userId);

	Page<Post> findByCategoryId(Long cateId, Pageable pageable);

}
