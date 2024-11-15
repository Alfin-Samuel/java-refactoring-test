package com.sap.refactoring.integration.controller;

import com.sap.refactoring.model.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/users";
    }

    @Test
    void testAddUser_Success() {
        UserDto newUser = new UserDto(null, "John Doe", "john.doe@example.com", Collections.singletonList("ROLE_USER"));

        ResponseEntity<UserDto> response = restTemplate.postForEntity(baseUrl + "/enroll", newUser, UserDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void testAddUser_MissingEmail_Failure() {
        UserDto newUser = new UserDto(null, "John Doe", null, Collections.singletonList("ROLE_USER"));

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/enroll", newUser, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testUpdateUser_Success() {
        UserDto newUser = new UserDto(null, "Jane Doe", "jane.doe@example.com", Collections.singletonList("ROLE_USER"));
        UserDto createdUser = restTemplate.postForEntity(baseUrl + "/enroll", newUser, UserDto.class).getBody();

        assertThat(createdUser).isNotNull();

        UserDto updatedUser = new UserDto(createdUser.getId(), "Jane Smith", "jane.smith@example.com", createdUser.getRoles());
        ResponseEntity<UserDto> response = restTemplate.exchange(
                baseUrl + "/edit/" + createdUser.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(updatedUser),
                UserDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Jane Smith");
    }

    @Test
    void testUpdateUser_NotFound_Failure() {
        UserDto updatedUser = new UserDto(999L, "Non Existent", "non.existent@example.com", Collections.singletonList("ROLE_USER"));

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/edit/999",
                HttpMethod.PUT,
                new HttpEntity<>(updatedUser),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("User with ID 999 not found.");
    }

    @Test
    void testDeleteUser_Success() {
        UserDto newUser = new UserDto(null, "Delete Me", "delete.me@example.com", Collections.singletonList("ROLE_USER"));
        UserDto createdUser = restTemplate.postForEntity(baseUrl + "/enroll", newUser, UserDto.class).getBody();

        assertThat(createdUser).isNotNull();

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/"+ createdUser.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void testDeleteUser_NotFound_Failure() {
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/999",
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void testGetAllUsers() {
        UserDto user1 = new UserDto(null, "User One", "user.one@example.com", Collections.singletonList("ROLE_USER"));
        UserDto user2 = new UserDto(null, "User Two", "user.two@example.com", Collections.singletonList("ROLE_ADMIN"));

        restTemplate.postForEntity(baseUrl + "/enroll", user1, UserDto.class);
        restTemplate.postForEntity(baseUrl + "/enroll", user2, UserDto.class);

        ResponseEntity<UserDto[]> response = restTemplate.getForEntity(baseUrl , UserDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        List<UserDto> users = Arrays.asList(response.getBody());
        assertThat(users).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void testFindUserByName_Success() {
        UserDto newUser = new UserDto(null, "Search Me", "search.me@example.com", Collections.singletonList("ROLE_USER"));
        restTemplate.postForEntity(baseUrl + "/enroll", newUser, UserDto.class);

        ResponseEntity<UserDto> response = restTemplate.getForEntity(baseUrl + "/search?name=Search Me", UserDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Search Me");
    }

    @Test
    void testFindUserByName_NotFound() {
        ResponseEntity<UserDto> response = restTemplate.getForEntity(baseUrl + "/search?name=Non Existent", UserDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
