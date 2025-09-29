package com.example.jwt_demo.controller;

import com.example.jwt_demo.dto.UserDto;
import com.example.jwt_demo.entity.AppUser;
import com.example.jwt_demo.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ADMIN only - list all users
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<UserDto> getAll(){
        return userRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    // GET user by id - ADMIN or owner
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or #id.toString() == principal.name or @securityService != null") // fallback check below
    public ResponseEntity<?> getById(@PathVariable Long id, Principal principal){
        var opt = userRepository.findById(id);
        if(opt.isEmpty()) return ResponseEntity.notFound().build();
        var user = opt.get();
        // allow if admin or owner
        if(principal != null && (principal.getName().equals(user.getUsername()) || hasAdmin(principal))) {
            return ResponseEntity.ok(toDto(user));
        }
        if(hasAdmin(principal)) return ResponseEntity.ok(toDto(user));
        return ResponseEntity.status(403).body("Forbidden");
    }

    // create user - public (or admin can create)
    @PostMapping
    public ResponseEntity<?> create(@RequestBody UserDto dto){
        if(userRepository.findByUsername(dto.getUsername()).isPresent()) return ResponseEntity.badRequest().body("Username exists");
        AppUser u = new AppUser();
        u.setUsername(dto.getUsername());
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        u.setRole(dto.getRole());
        userRepository.save(u);
        return ResponseEntity.ok(toDto(u));
    }

    // update user - owner or admin (user can update own password)
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UserDto dto, Principal principal){
        var opt = userRepository.findById(id);
        if(opt.isEmpty()) return ResponseEntity.notFound().build();
        var user = opt.get();
        if(principal == null) return ResponseEntity.status(401).build();
        boolean isAdmin = hasAdmin(principal);
        if(!isAdmin && !principal.getName().equals(user.getUsername())) return ResponseEntity.status(403).body("Forbidden");
        if(dto.getPassword() != null && !dto.getPassword().isBlank()) user.setPassword(passwordEncoder.encode(dto.getPassword()));
        if(isAdmin && dto.getRole() != null) user.setRole(dto.getRole());
        userRepository.save(user);
        return ResponseEntity.ok(toDto(user));
    }

    // DELETE user - ONLY ADMIN (require ROLE_ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id){
        if(!userRepository.existsById(id)) return ResponseEntity.notFound().build();
        userRepository.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }

    private UserDto toDto(AppUser u){
        UserDto d = new UserDto();
        d.setId(u.getId());
        d.setUsername(u.getUsername());
        d.setRole(u.getRole());
        // do not send password
        return d;
    }

    private boolean hasAdmin(Principal principal){
        // crude: check if principal has ROLE_ADMIN by loading user? It's simpler to check repository:
        var opt = userRepository.findByUsername(principal.getName());
        return opt.map(x -> x.getRole() != null && x.getRole().name().equals("ROLE_ADMIN")).orElse(false);
    }
}
