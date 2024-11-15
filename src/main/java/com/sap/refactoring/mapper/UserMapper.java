package com.sap.refactoring.mapper;

import com.sap.refactoring.entity.UserEntity;
import com.sap.refactoring.model.UserDto;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class UserMapper {

    // Converts User entity to UserDto
    public UserDto toDto(UserEntity user) {
        return Optional.ofNullable(user) // Wrap the user in an Optional
                .map(u -> new UserDto(
                        u.getId(),
                        u.getName(),
                        u.getEmail(),
                        u.getRoles() != null ? u.getRoles() : Collections.emptyList()
                ))
                .orElse(null); // Return null if user is null
    }

    // Converts UserDto to User entity
    public UserEntity toEntity(UserDto userDto) {
        return Optional.ofNullable(userDto) // Wrap the userDto in an Optional
                .map(dto -> {
                    UserEntity user = new UserEntity();
                    user.setId(dto.getId());
                    user.setName(dto.getName());
                    user.setEmail(dto.getEmail());
                    user.setRoles(dto.getRoles() != null ? dto.getRoles() : Collections.emptyList());
                    return user;
                })
                .orElse(null); // Return null if userDto is null
    }
}
