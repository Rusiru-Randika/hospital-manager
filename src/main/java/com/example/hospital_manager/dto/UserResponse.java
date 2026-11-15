package com.example.hospital_manager.dto;

import com.example.hospital_manager.model.AppUser;
import com.example.hospital_manager.model.Role;

public record UserResponse(
    Long id,
    String username,
    String fullName,
    Role role
) {
    public UserResponse(Long id, String username, Role role) {
        this(id, username, null, role);
    }

    public static UserResponse fromEntity(AppUser user) {
        if (user == null) return null;
        return new UserResponse(user.getId(), user.getUsername(), user.getFullName(), user.getRole());
    }
}
