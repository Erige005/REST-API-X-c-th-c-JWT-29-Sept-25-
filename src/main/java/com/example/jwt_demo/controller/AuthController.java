package com.example.jwt_demo.controller;

import com.example.jwt_demo.dto.AuthRequest;
import com.example.jwt_demo.dto.AuthResponse;
import com.example.jwt_demo.dto.UserDto;
import com.example.jwt_demo.entity.AppUser;
import com.example.jwt_demo.repository.UserRepository;
import com.example.jwt_demo.entity.Role;
import com.example.jwt_demo.security.JwtUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        org.springframework.security.core.userdetails.UserDetails userDetails =
                new org.springframework.security.core.userdetails.User(request.getUsername(), "", java.util.List.of());
        // Actually get UserDetails from userRepository via jwtUtils generation:
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        var userDetailsLoaded = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(user.getRole().name())))
                .build();
        String token = jwtUtils.generateToken(userDetailsLoaded);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    // register - public (create USER by default, ADMIN can create ADMIN via payload)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto dto){
        if(userRepository.findByUsername(dto.getUsername()).isPresent()){
            return ResponseEntity.badRequest().body("Username already exists");
        }
        AppUser u = new AppUser();
        u.setUsername(dto.getUsername());
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        u.setRole(dto.getRole() == null ? Role.ROLE_USER : dto.getRole());
        userRepository.save(u);
        return ResponseEntity.ok("User created");
    }
}
