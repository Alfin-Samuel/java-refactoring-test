package com.sap.refactoring.unit.service;

import com.sap.refactoring.entity.UserEntity;
import com.sap.refactoring.exceptions.InvalidUserException;
import com.sap.refactoring.exceptions.UserNotFoundException;
import com.sap.refactoring.mapper.UserMapper;
import com.sap.refactoring.model.UserDto;
import com.sap.refactoring.repository.UserRepository;
import com.sap.refactoring.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UserDto userDto;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1L, "John Doe", "john.doe@example.com", List.of("ROLE_USER"));
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("John Doe");
        userEntity.setEmail("john.doe@example.com");
        userEntity.setRoles(List.of("ROLE_USER"));
    }

    @Test
    void testAddUser_WhenUserIsValid_ShouldReturnUserDto() throws InvalidUserException {
        when(userMapper.toEntity(userDto)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        UserDto result = userService.addUser(userDto);

        assertThat(result).isEqualTo(userDto);
        verify(userRepository).save(userEntity);
        verify(userMapper).toDto(userEntity);
    }

    @Test
    void testAddUser_WhenDataIntegrityViolationOccurs_ShouldThrowInvalidUserException() {
        when(userMapper.toEntity(userDto)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenThrow(new DataIntegrityViolationException("Duplicate email"));

        assertThatThrownBy(() -> userService.addUser(userDto))
                .isInstanceOf(InvalidUserException.class)
                .hasMessage("User data is invalid or violates integrity constraints.");

        verify(userRepository).save(userEntity);
    }

    @Test
    void testUpdateUser_WhenUserExists_ShouldUpdateAndReturnUserDto() throws UserNotFoundException {
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(userEntity));
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        UserDto result = userService.updateUser(userDto.getId(), userDto);

        assertThat(result).isEqualTo(userDto);
        verify(userRepository).save(userEntity);
        verify(userMapper).toDto(userEntity);
    }

    @Test
    void testUpdateUser_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(userDto.getId(), userDto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User with ID " + userDto.getId() + " not found");

        verify(userRepository, never()).save(any());
    }

    @Test
    void testDeleteUser_WhenUserExists_ShouldReturnTrue() {
        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));

        boolean result = userService.deleteUser(userEntity.getId());

        assertThat(result).isTrue();
        verify(userRepository).deleteById(userEntity.getId());
    }

    @Test
    void testDeleteUser_WhenUserNotFound_ShouldReturnFalse() {
        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.empty());

        boolean result = userService.deleteUser(userEntity.getId());

        assertThat(result).isFalse();
        verify(userRepository, never()).deleteById(userEntity.getId());
    }

    @Test
    void testFindUserById_WhenUserExists_ShouldReturnUserDto() throws UserNotFoundException {
        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        UserDto result = userService.findUserById(userEntity.getId());

        assertThat(result).isEqualTo(userDto);
        verify(userRepository).findById(userEntity.getId());
        verify(userMapper).toDto(userEntity);
    }

    @Test
    void testFindUserById_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserById(userEntity.getId()))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User with ID " + userEntity.getId() + " not found");

        verify(userRepository).findById(userEntity.getId());
    }

    @Test
    void testGetAllUsers_ShouldReturnListOfUserDto() {
        when(userRepository.findAll()).thenReturn(List.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        List<UserDto> result = userService.getAllUsers();

        assertThat(result).containsExactly(userDto);
        verify(userRepository).findAll();
        verify(userMapper).toDto(userEntity);
    }

    @Test
    void testFindUserByName_WhenUserExists_ShouldReturnUserDto() {
        when(userRepository.findByName(userEntity.getName())).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        UserDto result = userService.findUserByName(userEntity.getName());

        assertThat(result).isEqualTo(userDto);
        verify(userRepository).findByName(userEntity.getName());
        verify(userMapper).toDto(userEntity);
    }

    @Test
    void testFindUserByName_WhenUserNotFound_ShouldReturnNull() {
        when(userRepository.findByName(userEntity.getName())).thenReturn(Optional.empty());

        UserDto result = userService.findUserByName(userEntity.getName());

        assertThat(result).isNull();
        verify(userRepository).findByName(userEntity.getName());
    }
}
