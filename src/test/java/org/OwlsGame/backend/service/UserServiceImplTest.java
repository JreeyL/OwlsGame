package org.OwlsGame.backend.service;

import org.OwlsGame.backend.dao.UserRepository;
import org.OwlsGame.backend.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        // 设置测试用户
        testUser = new User("John", "Doe", "password123", "john.doe@example.com");
        testUser.setId(1L);

        anotherUser = new User("Jane", "Smith", "password456", "jane.smith@example.com");
        anotherUser.setId(2L);
    }

    @Test
    void whenCreateUser_thenUserIsSaved() {
        // given
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        User savedUser = userService.createUser(testUser);

        // then
        assertNotNull(savedUser);
        assertEquals(testUser.getEmail(), savedUser.getEmail());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void whenGetUserById_thenReturnUser() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // when
        Optional<User> foundUser = userService.getUserById(1L);

        // then
        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getId(), foundUser.get().getId());
        assertEquals(testUser.getEmail(), foundUser.get().getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void whenGetUserByNonExistingId_thenReturnEmpty() {
        // given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // when
        Optional<User> foundUser = userService.getUserById(999L);

        // then
        assertFalse(foundUser.isPresent());
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void whenGetAllUsers_thenReturnUsersList() {
        // given
        List<User> users = Arrays.asList(testUser, anotherUser);
        when(userRepository.findAll()).thenReturn(users);

        // when
        List<User> foundUsers = userService.getAllUsers();

        // then
        assertEquals(2, foundUsers.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void whenUpdateUser_thenUserIsUpdated() {
        // given
        testUser.setFirstname("Johnny");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        User updatedUser = userService.updateUser(testUser);

        // then
        assertEquals("Johnny", updatedUser.getFirstname());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void whenDeleteUser_thenUserIsDeleted() {
        // given
        Long userId = 1L;
        doNothing().when(userRepository).deleteById(userId);

        // when
        userService.deleteUser(userId);

        // then
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void whenGetUserByEmail_thenReturnUser() {
        // given
        String email = "john.doe@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // when
        Optional<User> foundUser = userService.getUserByEmail(email);

        // then
        assertTrue(foundUser.isPresent());
        assertEquals(email, foundUser.get().getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void whenGetUserByNonExistingEmail_thenReturnEmpty() {
        // given
        String email = "nonexisting@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when
        Optional<User> foundUser = userService.getUserByEmail(email);

        // then
        assertFalse(foundUser.isPresent());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void whenValidateCredentialsWithCorrectDetails_thenReturnTrue() {
        // given
        String email = "john.doe@example.com";
        String password = "password123";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // when
        boolean isValid = userService.validateCredentials(email, password);

        // then
        assertTrue(isValid);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void whenValidateCredentialsWithIncorrectPassword_thenReturnFalse() {
        // given
        String email = "john.doe@example.com";
        String wrongPassword = "wrongPassword";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // when
        boolean isValid = userService.validateCredentials(email, wrongPassword);

        // then
        assertFalse(isValid);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void whenValidateCredentialsWithNonExistingEmail_thenReturnFalse() {
        // given
        String email = "nonexisting@example.com";
        String password = "password123";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when
        boolean isValid = userService.validateCredentials(email, password);

        // then
        assertFalse(isValid);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void whenLockAccount_thenAccountIsLocked() {
        // given
        String email = "john.doe@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        userService.lockAccount(email);

        // then
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).save(any(User.class));
        assertTrue(testUser.isLocked());
    }

    @Test
    void whenLockNonExistingAccount_thenNoActionIsTaken() {
        // given
        String email = "nonexisting@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when
        userService.lockAccount(email);

        // then
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void whenUnlockAccount_thenAccountIsUnlocked() {
        // given
        String email = "john.doe@example.com";
        testUser.setLocked(true);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        userService.unlockAccount(email);

        // then
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).save(any(User.class));
        assertFalse(testUser.isLocked());
    }

    @Test
    void whenUnlockNonExistingAccount_thenNoActionIsTaken() {
        // given
        String email = "nonexisting@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when
        userService.unlockAccount(email);

        // then
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void whenCheckIfAccountIsLocked_thenReturnCorrectStatus() {
        // given
        String email = "john.doe@example.com";
        testUser.setLocked(true);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // when
        boolean isLocked = userService.isAccountLocked(email);

        // then
        assertTrue(isLocked);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void whenCheckIfNonExistingAccountIsLocked_thenReturnFalse() {
        // given
        String email = "nonexisting@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when
        boolean isLocked = userService.isAccountLocked(email);

        // then
        assertFalse(isLocked);
        verify(userRepository, times(1)).findByEmail(email);
    }
}