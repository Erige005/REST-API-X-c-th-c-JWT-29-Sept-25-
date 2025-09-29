package com.example.jwt_demo.controller;

import com.example.jwt_demo.dto.BlogDto;
import com.example.jwt_demo.entity.Blog;
import com.example.jwt_demo.entity.AppUser;
import com.example.jwt_demo.repository.BlogRepository;
import com.example.jwt_demo.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    public BlogController(BlogRepository blogRepository, UserRepository userRepository){
        this.blogRepository = blogRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<BlogDto> getAll(){
        return blogRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        return blogRepository.findById(id).map(b -> ResponseEntity.ok(toDto(b))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create blog - authenticated users
    @PostMapping
    public ResponseEntity<?> create(@RequestBody BlogDto dto, Principal principal){
        if(principal == null) return ResponseEntity.status(401).build();
        var user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Blog b = new Blog();
        b.setTitle(dto.getTitle());
        b.setContent(dto.getContent());
        b.setOwner(user);
        blogRepository.save(b);
        return ResponseEntity.ok(toDto(b));
    }

    // Update blog - only owner or ADMIN
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody BlogDto dto, Principal principal){
        var opt = blogRepository.findById(id);
        if(opt.isEmpty()) return ResponseEntity.notFound().build();
        Blog b = opt.get();
        if(principal == null) return ResponseEntity.status(401).build();
        var current = userRepository.findByUsername(principal.getName()).orElseThrow();
        boolean isAdmin = current.getRole().name().equals("ROLE_ADMIN");
        boolean isOwner = b.getOwner() != null && b.getOwner().getId().equals(current.getId());
        if(!isAdmin && !isOwner) return ResponseEntity.status(403).body("Forbidden");
        b.setTitle(dto.getTitle()!=null?dto.getTitle():b.getTitle());
        b.setContent(dto.getContent()!=null?dto.getContent():b.getContent());
        blogRepository.save(b);
        return ResponseEntity.ok(toDto(b));
    }

    // Delete blog - allow owner or admin
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, Principal principal){
        var opt = blogRepository.findById(id);
        if(opt.isEmpty()) return ResponseEntity.notFound().build();
        Blog b = opt.get();
        if(principal == null) return ResponseEntity.status(401).build();
        var current = userRepository.findByUsername(principal.getName()).orElseThrow();
        boolean isAdmin = current.getRole().name().equals("ROLE_ADMIN");
        boolean isOwner = b.getOwner() != null && b.getOwner().getId().equals(current.getId());
        if(!isAdmin && !isOwner) return ResponseEntity.status(403).body("Forbidden");
        blogRepository.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }

    private BlogDto toDto(Blog b){
        BlogDto d = new BlogDto();
        d.setId(b.getId());
        d.setTitle(b.getTitle());
        d.setContent(b.getContent());
        return d;
    }
}
