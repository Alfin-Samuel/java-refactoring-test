package com.sap.refactoring.controller;

import com.sap.refactoring.exceptions.UserNotFoundException;
import com.sap.refactoring.model.UserDto;
import com.sap.refactoring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing user-related operations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	/**
	 * Creates a new user.
	 *
	 * @param userDto the user data to create.
	 * @return the created user.
	 */
	@PostMapping("/enroll")
	public ResponseEntity<UserDto> addUser(@RequestBody UserDto userDto) {
		return new ResponseEntity<>(userService.addUser(userDto), HttpStatus.CREATED);
	}

	/**
	 * Updates an existing user.
	 *
	 * @param id      the ID of the user to update.
	 * @param userDto the updated user data.
	 * @return the updated user.
	 */
	@PutMapping("edit/{id}")
	public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
		UserDto updatedUser = userService.updateUser(id, userDto);
		return ResponseEntity.ok(updatedUser);
	}

	/**
	 * Deletes a user.
	 *
	 * @param id the ID of the user to delete.
	 * @return a 204 status if deleted.
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Retrieves all users.
	 *
	 * @return a list of all users.
	 */
	@GetMapping
	public ResponseEntity<List<UserDto>> getAllUsers() {
		List<UserDto> users = userService.getAllUsers();
		return ResponseEntity.ok(users);
	}
	/**
	 * Finds a user by name.
	 *
	 * @param name the name of the user to find.
	 * @return the user if found.
	 * @throws UserNotFoundException if the user is not found.
	 */
	@GetMapping("/search")
	public ResponseEntity<UserDto> findUser(@RequestParam("name") String name) {
		UserDto user = userService.findUserByName(name);
		if (user == null) {
			throw new UserNotFoundException("User with name '" + name + "' not found.");
		}
		return ResponseEntity.ok(user);
	}
}
