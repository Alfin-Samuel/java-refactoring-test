package com.sap.refactoring.service;

import com.sap.refactoring.entity.UserEntity;
import com.sap.refactoring.exceptions.InvalidUserException;
import com.sap.refactoring.exceptions.UserNotFoundException;
import com.sap.refactoring.mapper.UserMapper;
import com.sap.refactoring.model.UserDto;
import com.sap.refactoring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer responsible for handling user business logic.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserMapper userMapper;

  /*  @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }
*/

    /**
     * Add a new user to the system.
     *
     * @param userDto the data transfer object containing user data.
     * @return the created user DTO.
     * @throws InvalidUserException if the user data is invalid.
     */
    @Transactional
    public UserDto addUser(UserDto userDto) throws InvalidUserException {
        validateUserDto(userDto);
        checkEmailUniqueness(userDto.getEmail());

        try {
            UserEntity userEntity = userMapper.toEntity(userDto);
            UserEntity savedUser = userRepository.save(userEntity); // Save the user to the database
            logger.info("User with email {} created successfully", savedUser.getEmail());
            return userMapper.toDto(savedUser);
        } catch (DataIntegrityViolationException e) {
            logger.error("Error saving user with email {}: {}", userDto.getEmail(), e.getMessage());
            throw new InvalidUserException("User data is invalid or violates integrity constraints.");
        }
    }

    /**
     * Update an existing user by ID.
     *
     * @param userId  the ID of the user to update.
     * @param userDto the updated user data.
     * @return the updated user DTO.
     * @throws UserNotFoundException if the user is not found.
     */
    @Transactional
    public UserDto updateUser(Long userId, UserDto userDto) throws UserNotFoundException {
        validateUserDto(userDto);

        Optional<UserEntity> existingUser = userRepository.findById(userId);

        if (!existingUser.isPresent()) {
            throw new UserNotFoundException("User with ID " + userId + " not found.");
        }

        UserEntity user = existingUser.get();
        if (!user.getEmail().equals(userDto.getEmail())) {
            checkEmailUniqueness(userDto.getEmail());
        }
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setRoles(userDto.getRoles());
        UserEntity updatedUser = userRepository.save(user);
        logger.info("User with ID {} updated successfully", userId);
        return userMapper.toDto(updatedUser);
    }

    /**
     * Delete a user by ID.
     *
     * @param userId the ID of the user to delete.
     * @return true if the user was deleted, false otherwise.
     */
    @Transactional
    public boolean deleteUser(Long userId) {
        Optional<UserEntity> existingUser = userRepository.findById(userId);

        return existingUser.map(user -> {
            userRepository.deleteById(userId);
            logger.info("User with ID {} deleted successfully", userId);
            return true;
        }).orElseGet(() -> {
            logger.warn("User with ID {} not found for deletion", userId);
            return false;
        });
    }

    /**
     * Find a user by ID.
     *
     * @param userId the ID of the user to find.
     * @return the user DTO if found, or throws UserNotFoundException if not found.
     * @throws UserNotFoundException if the user with the given ID does not exist.
     */
    public UserDto findUserById(Long userId) throws UserNotFoundException {
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> {
                    logger.warn("User with ID {} not found", userId);
                    return new UserNotFoundException("User with ID " + userId + " not found.");
                });
    }

    /**
     * Get a list of all users.
     *
     * @return a list of all user DTOs.
     */
    public List<UserDto> getAllUsers() {
        try {
            List<UserEntity> users = userRepository.findAll();
            logger.info("Fetched {} users from the database", users.size());
            return users.stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching users from the database: {}", e.getMessage());
            throw new RuntimeException("An unexpected error occurred while fetching users.");
        }
    }

    /**
     * Find a user by name.
     *
     * @param name the name of the user to find.
     * @return the user DTO if found, otherwise null.
     */
    public UserDto findUserByName(String name) {
        return userRepository.findByName(name)
                .map(userMapper::toDto)
                .orElse(null);
    }

    /**
     * Check if the email already exists in the database.
     *
     * @param email the email to check.
     * @throws InvalidUserException if the email is already in use.
     */
    private void checkEmailUniqueness(String email) throws InvalidUserException {
        if (userRepository.findByEmail(email).isPresent()) {
            logger.error("User with email {} already exists", email);
            throw new InvalidUserException("A user with this email already exists.");
        }
    }

    /**
     * Validate that user DTO has at least one role.
     *
     * @param userDto the user data to validate.
     * @throws InvalidUserException if validation fails.
     */
    private void validateUserDto(UserDto userDto) throws InvalidUserException {
        if (userDto == null || userDto.getRoles() == null || userDto.getRoles().isEmpty()) {
            logger.error("User data is invalid: missing roles.");
            throw new InvalidUserException("User must have at least one role.");
        }
    }
}
