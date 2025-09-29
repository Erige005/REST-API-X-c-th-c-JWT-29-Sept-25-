package com.example.jwt_demo.dto;

import com.example.jwt_demo.entity.Role;

public class UserDto {
    private Long id;
    private String username;
    private String password; // only for create/update
    private Role role;
    // getters/setters
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getUsername(){return username;}
    public void setUsername(String u){this.username=u;}
    public String getPassword(){return password;}
    public void setPassword(String p){this.password=p;}
    public Role getRole(){return role;}
    public void setRole(Role r){this.role=r;}
}
