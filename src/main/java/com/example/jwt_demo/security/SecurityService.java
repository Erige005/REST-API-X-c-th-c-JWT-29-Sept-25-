package com.example.jwt_demo.security;

import com.example.jwt_demo.entity.Blog;
import com.example.jwt_demo.repository.BlogRepository;
import org.springframework.stereotype.Service;

@Service("securityService")
public class SecurityService {
    private final BlogRepository blogRepository;
    public SecurityService(BlogRepository blogRepository){this.blogRepository=blogRepository;}

    // Check if username is owner of blog id
    public boolean isBlogOwner(Long blogId, String username){
        return blogRepository.findById(blogId)
                .map(Blog::getOwner)
                .map(owner -> owner.getUsername().equals(username))
                .orElse(false);
    }
}
