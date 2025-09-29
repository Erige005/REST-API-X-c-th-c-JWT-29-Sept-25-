package com.example.jwt_demo.repository;

import com.example.jwt_demo.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findByOwnerId(Long ownerId);
}
