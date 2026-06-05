package com.portfolio.dto;

import java.util.List;

public class PublicProfileDto {
    private String name;
    private List<String> roles;

    // --- CONSTRUCTORS ---
    public PublicProfileDto() {}

    public PublicProfileDto(String name, List<String> roles) {
        this.name = name;
        this.roles = roles;
    }

    // --- GETTERS AND SETTERS ---
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}