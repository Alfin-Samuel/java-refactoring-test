package com.sap.refactoring.web.controller;

import com.sap.refactoring.exceptions.InvalidUserException;
import com.sap.refactoring.exceptions.UserNotFoundException;
import com.sap.refactoring.model.UserDto;
import com.sap.refactoring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing user-related operations.
 */
@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	/**
	 * Constructs a UserController with injected UserService.
	 *
	 * @param userService the user service for handling business logic.
	 */
	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	/**
	 * Creates a new user.
	 *
	 * @param userDto the user data to create.
	 * @return the created user.
	 */
	@PostMapping("/add")
	public ResponseEntity<UserDto> addUser(@RequestBody UserDto userDto) {
		try {
			return new ResponseEntity<>(userService.addUser(userDto), HttpStatus.CREATED);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(null);
		} catch (InvalidUserException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}

	/**
	 * Updates an existing user.
	 *
	 * @param id      the ID of the user to update.
	 * @param userDto the updated user data.
	 * @return the updated user or 404 if not found.
	 */
	@PutMapping("update/{id}")
	public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
		try {
			UserDto updatedUser = userService.updateUser(id, userDto);
			if (updatedUser != null) {
				return ResponseEntity.ok(updatedUser);
			}
		} catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	/**
	 * Deletes a user.
	 *
	 * @param id the ID of the user to delete.
	 * @return a 204 status if deleted or 404 if not found.
	 */
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		if (userService.deleteUser(id)) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	/**
	 * Retrieves all users.
	 *
	 * @return a list of all users.
	 */
	@GetMapping("find")
	public ResponseEntity<List<UserDto>> getAllUsers() {
		List<UserDto> users = userService.getAllUsers();
		return users.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(users);
	}

	/**
	 * Finds a user by name.
	 *
	 * @param name the name of the user to find.
	 * @return the user if found, or 404 if not found.
	 */
	@GetMapping("/search")
	public ResponseEntity<UserDto> findUser(@RequestParam("name") String name) {
		UserDto user = userService.findUserByName(name);
		if (user != null) {
			return ResponseEntity.ok(user);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}
}
