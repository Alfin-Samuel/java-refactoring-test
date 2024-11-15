package com.sap.refactoring.integration.respository;

import com.sap.refactoring.entity.UserEntity;
import com.sap.refactoring.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveUser_Success() {
        UserEntity user = new UserEntity();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setRoles(List.of("USER"));

        UserEntity savedUser = userRepository.save(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("John Doe");
        assertThat(savedUser.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(savedUser.getRoles()).containsExactly("USER");
    }

    @Test
    void testFindUserByEmail_Success() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setName("Jane Doe");
        user.setEmail("jane.doe@example.com");
        user.setRoles(List.of("ADMIN"));
        userRepository.save(user);

        // Act
        Optional<UserEntity> foundUser = userRepository.findByEmail("jane.doe@example.com");

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Jane Doe");
        assertThat(foundUser.get().getRoles()).contains("ADMIN");
    }

    @Test
    void testFindUserByEmail_NotFound() {
        Optional<UserEntity> foundUser = userRepository.findByEmail("nonexistent@example.com");

        assertThat(foundUser).isEmpty();
    }

    @Test
    void testDeleteUserById_Success() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setName("Mark Spencer");
        user.setEmail("mark.spencer@example.com");
        user.setRoles(List.of("USER"));
        UserEntity savedUser = userRepository.save(user);

        // Act
        userRepository.deleteById(savedUser.getId());
        Optional<UserEntity> deletedUser = userRepository.findById(savedUser.getId());

        // Assert
        assertThat(deletedUser).isEmpty();
    }

    @Test
    @Sql(scripts = "/test-data.sql") // Load predefined data from SQL script
    void testFindUserByName_Success() {
        Optional<UserEntity> user = userRepository.findByName("Test User");

        assertThat(user).isPresent();
        assertThat(user.get().getEmail()).isEqualTo("test.user@example.com");
    }
}
