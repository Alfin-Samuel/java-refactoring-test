package com.sap.refactoring.integration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.refactoring.model.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserServiceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddUser_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john.doe@example.com");
        userDto.setRoles(Collections.singletonList("USER"));

        mockMvc.perform(post("/users/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void testAddUser_InvalidData() throws Exception {
        UserDto userDto = new UserDto(); // Missing roles

        mockMvc.perform(post("/users/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUser_NotFound() throws Exception {
        Long nonExistentUserId = 999L;
        UserDto userDto = new UserDto();
        userDto.setName("Non-existent User");
        userDto.setEmail("nonexistent.email@example.com");
        userDto.setRoles(Collections.singletonList("USER"));

        mockMvc.perform(put("/users/edit/{id}", nonExistentUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        // Add a user to the database
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("test.user@example.com");
        userDto.setRoles(Collections.singletonList("USER"));

        String response = mockMvc.perform(post("/users/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andReturn().getResponse().getContentAsString();

        UserDto createdUser = objectMapper.readValue(response, UserDto.class);

        // Delete the user
        mockMvc.perform(delete("/users/{id}", createdUser.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUser_NotFound() throws Exception {
        Long nonExistentUserId = 999L;

        mockMvc.perform(delete("/users/{id}", nonExistentUserId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetAllUsers_Success() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testFindUserByName_Success() throws Exception {
        String userName = "John Doe";

        mockMvc.perform(get("/users/search")
                        .param("name", userName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(userName));
    }

    @Test
    void testFindUserByName_NotFound() throws Exception {
        String nonExistentUserName = "Non Existent User";

        mockMvc.perform(get("/users/search")
                        .param("name", nonExistentUserName))
                .andExpect(status().isNotFound());
    }
}
