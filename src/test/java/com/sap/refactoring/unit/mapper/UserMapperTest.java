package com.sap.refactoring.unit.mapper;

import com.sap.refactoring.entity.UserEntity;
import com.sap.refactoring.mapper.UserMapper;
import com.sap.refactoring.model.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void testToDto_WhenUserEntityIsNotNull_ShouldMapToDtoCorrectly() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("John Doe");
        userEntity.setEmail("john.doe@example.com");
        userEntity.setRoles(List.of("ROLE_USER", "ROLE_ADMIN"));

        // Act
        UserDto userDto = userMapper.toDto(userEntity);

        // Assert
        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isEqualTo(userEntity.getId());
        assertThat(userDto.getName()).isEqualTo(userEntity.getName());
        assertThat(userDto.getEmail()).isEqualTo(userEntity.getEmail());
        assertThat(userDto.getRoles()).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    void testToDto_WhenUserEntityIsNull_ShouldReturnNull() {
        // Act
        UserDto userDto = userMapper.toDto(null);

        // Assert
        assertThat(userDto).isNull();
    }

    @Test
    void testToDto_WhenRolesAreNull_ShouldReturnDtoWithEmptyRoles() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setId(2L);
        userEntity.setName("Jane Doe");
        userEntity.setEmail("jane.doe@example.com");
        userEntity.setRoles(null);

        // Act
        UserDto userDto = userMapper.toDto(userEntity);

        // Assert
        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isEqualTo(2L);
        assertThat(userDto.getName()).isEqualTo("Jane Doe");
        assertThat(userDto.getEmail()).isEqualTo("jane.doe@example.com");
        assertThat(userDto.getRoles()).isEmpty();
    }

    @Test
    void testToEntity_WhenUserDtoIsNotNull_ShouldMapToEntityCorrectly() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setId(3L);
        userDto.setName("Alice Doe");
        userDto.setEmail("alice.doe@example.com");
        userDto.setRoles(List.of("ROLE_USER"));

        // Act
        UserEntity userEntity = userMapper.toEntity(userDto);

        // Assert
        assertThat(userEntity).isNotNull();
        assertThat(userEntity.getId()).isEqualTo(userDto.getId());
        assertThat(userEntity.getName()).isEqualTo(userDto.getName());
        assertThat(userEntity.getEmail()).isEqualTo(userDto.getEmail());
        assertThat(userEntity.getRoles()).containsExactly("ROLE_USER");
    }

    @Test
    void testToEntity_WhenUserDtoIsNull_ShouldReturnNull() {
        // Act
        UserEntity userEntity = userMapper.toEntity(null);

        // Assert
        assertThat(userEntity).isNull();
    }

    @Test
    void testToEntity_WhenRolesAreNull_ShouldReturnEntityWithEmptyRoles() {
        // Arrange
        UserDto userDto = UserDto.builder()
                .id(4L)
                .name("Bob Doe")
                .email("bob.doe@example.com")
                .roles(null)
                .build();

        // Act
        UserEntity userEntity = userMapper.toEntity(userDto);

        // Assert
        assertThat(userEntity).isNotNull();
        assertThat(userEntity.getId()).isEqualTo(4L);
        assertThat(userEntity.getName()).isEqualTo("Bob Doe");
        assertThat(userEntity.getEmail()).isEqualTo("bob.doe@example.com");
        assertThat(userEntity.getRoles()).isEmpty();
    }
}
