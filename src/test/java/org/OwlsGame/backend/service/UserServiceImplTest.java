package org.OwlsGame.backend.service;

import org.OwlsGame.backend.dao.UserRepository;
import org.OwlsGame.backend.dto.UserRegisterDto;
import org.OwlsGame.backend.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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
    private User lockedUser;

    @BeforeEach
    void setUp() {
        // 设置测试用户
        testUser = new User("John", "Doe", "password123", "john.doe@example.com");
        testUser.setId(1L);

        anotherUser = new User("Jane", "Smith", "password456", "jane.smith@example.com");
        anotherUser.setId(2L);

        // 设置已锁定的测试用户
        lockedUser = new User("Locked", "User", "password789", "locked.user@example.com");
        lockedUser.setId(3L);
        lockedUser.setLocked(true);
        lockedUser.setLockUntil(LocalDateTime.now().plusMinutes(10)); // 锁定10分钟
        lockedUser.setLoginAttempts(3);
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
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        boolean isValid = userService.validateCredentials(email, password);

        // then
        assertTrue(isValid);
        assertEquals(0, testUser.getLoginAttempts()); // 登录成功后尝试次数应重置
        assertFalse(testUser.isLocked()); // 登录成功后账户应解锁
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void whenValidateCredentialsWithIncorrectPassword_thenReturnFalse() {
        // given
        String email = "john.doe@example.com";
        String wrongPassword = "wrongPassword";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        boolean isValid = userService.validateCredentials(email, wrongPassword);

        // then
        assertFalse(isValid);
        assertEquals(1, testUser.getLoginAttempts()); // 登录失败后尝试次数应增加
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).save(testUser);
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
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void whenValidateCredentialsTooManyAttempts_thenAccountIsLocked() {
        // given
        String email = "john.doe@example.com";
        String wrongPassword = "wrongPassword";
        testUser.setLoginAttempts(2); // 设置已有2次失败尝试
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        boolean isValid = userService.validateCredentials(email, wrongPassword);

        // then
        assertFalse(isValid);
        assertEquals(3, testUser.getLoginAttempts()); // 登录失败后尝试次数应为3
        assertTrue(testUser.isLocked()); // 应被锁定
        assertNotNull(testUser.getLockUntil()); // 应设置解锁时间
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(2)).save(testUser); // 一次增加尝试次数，一次锁定账户
    }

    @Test
    void whenValidateCredentialsWithLockedAccount_thenReturnFalse() {
        // given
        String email = "locked.user@example.com";
        String password = "password789";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(lockedUser));

        // when
        boolean isValid = userService.validateCredentials(email, password);

        // then
        assertFalse(isValid);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void whenIncreaseLoginAttempts_thenAttemptsAreIncreased() {
        // given
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        userService.increaseLoginAttempts(testUser);

        // then
        assertEquals(1, testUser.getLoginAttempts());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void whenResetLoginAttempts_thenAttemptsAreReset() {
        // given
        testUser.setLoginAttempts(3);
        testUser.setLocked(true);
        testUser.setLockUntil(LocalDateTime.now().plusMinutes(5));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        userService.resetLoginAttempts(testUser);

        // then
        assertEquals(0, testUser.getLoginAttempts());
        assertFalse(testUser.isLocked());
        assertNull(testUser.getLockUntil());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void whenGetLoginAttempts_thenReturnCorrectNumber() {
        // given
        testUser.setLoginAttempts(2);

        // when
        int attempts = userService.getLoginAttempts(testUser);

        // then
        assertEquals(2, attempts);
    }

    @Test
    void whenLockAccount_thenAccountIsLocked() {
        // given
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        int lockMinutes = 10;

        // when
        userService.lockAccount(testUser, lockMinutes);

        // then
        assertTrue(testUser.isLocked());
        assertNotNull(testUser.getLockUntil());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void whenUnlockAccount_thenAccountIsUnlocked() {
        // given
        lockedUser.setLoginAttempts(3);
        lockedUser.setLocked(true);
        lockedUser.setLockUntil(LocalDateTime.now().plusMinutes(5));
        when(userRepository.save(any(User.class))).thenReturn(lockedUser);

        // when
        userService.unlockAccount(lockedUser);

        // then
        assertFalse(lockedUser.isLocked());
        assertNull(lockedUser.getLockUntil());
        assertEquals(0, lockedUser.getLoginAttempts());
        verify(userRepository, times(1)).save(lockedUser);
    }

    @Test
    void whenIsAccountLocked_withLockedAccount_thenReturnTrue() {
        // given
        User user = new User();
        user.setLocked(true);
        user.setLockUntil(LocalDateTime.now().plusMinutes(5)); // 锁定5分钟

        // when
        boolean isLocked = userService.isAccountLocked(user);

        // then
        assertTrue(isLocked);
    }

    @Test
    void whenIsAccountLocked_withUnlockedAccount_thenReturnFalse() {
        // given
        User user = new User();
        user.setLocked(false);

        // when
        boolean isLocked = userService.isAccountLocked(user);

        // then
        assertFalse(isLocked);
    }

    @Test
    void whenIsAccountLocked_withExpiredLock_thenUnlockAndReturnFalse() {
        // given
        User user = new User();
        user.setLocked(true);
        user.setLockUntil(LocalDateTime.now().minusMinutes(5)); // 锁定已过期
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        boolean isLocked = userService.isAccountLocked(user);

        // then
        assertFalse(isLocked);
        assertFalse(user.isLocked()); // 应被解锁
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void whenRegisterUser_withValidData_thenReturnSavedUser() {
        // given
        UserRegisterDto registerDto = new UserRegisterDto();
        registerDto.setFirstname("New");
        registerDto.setLastname("User");
        registerDto.setEmail("new.user@example.com");
        registerDto.setPassword("password123");

        User newUser = new User("New", "User", "password123", "new.user@example.com");

        when(userRepository.findByEmail(registerDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // when
        User savedUser = userService.registerUser(registerDto);

        // then
        assertNotNull(savedUser);
        assertEquals(registerDto.getEmail(), savedUser.getEmail());
        assertEquals(registerDto.getFirstname(), savedUser.getFirstname());
        assertEquals(registerDto.getLastname(), savedUser.getLastname());
        assertEquals(0, savedUser.getLoginAttempts());
        assertFalse(savedUser.isLocked());
        verify(userRepository, times(1)).findByEmail(registerDto.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void whenRegisterUser_withExistingEmail_thenThrowException() {
        // given
        UserRegisterDto registerDto = new UserRegisterDto();
        registerDto.setEmail("john.doe@example.com"); // 已存在的邮箱

        when(userRepository.findByEmail(registerDto.getEmail())).thenReturn(Optional.of(testUser));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(registerDto));
        verify(userRepository, times(1)).findByEmail(registerDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void whenLogin_withValidCredentials_thenReturnUser() {
        // given
        String email = "john.doe@example.com";
        String password = "password123";

        // 为 login 方法创建一个局部 mock
        UserServiceImpl spyUserService = spy(userService);

        // 模拟 validateCredentials 方法返回 true
        doReturn(true).when(spyUserService).validateCredentials(email, password);

        // 设置 userRepository.findByEmail 的行为，因为 login 方法会调用它
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // when
        User loggedInUser = spyUserService.login(email, password);

        // then
        assertNotNull(loggedInUser);
        assertEquals(email, loggedInUser.getEmail());
        // 验证 findByEmail 被调用过，不用关心具体调用次数
        verify(userRepository, atLeastOnce()).findByEmail(email);
    }

    @Test
    void whenLogin_withInvalidCredentials_thenReturnNull() {
        // given
        String email = "john.doe@example.com";
        String wrongPassword = "wrongPassword";

        // 为 login 方法创建一个局部 mock
        UserServiceImpl spyUserService = spy(userService);

        // 模拟 validateCredentials 方法返回 false
        doReturn(false).when(spyUserService).validateCredentials(email, wrongPassword);

        // 设置 userRepository.findByEmail 的行为，因为 login 方法会调用它
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // when
        User loggedInUser = spyUserService.login(email, wrongPassword);

        // then
        assertNull(loggedInUser);
        // 验证 findByEmail 被调用过，不用关心具体调用次数
        verify(userRepository, atLeastOnce()).findByEmail(email);
    }

    @Test
    void whenLogin_withNonExistingEmail_thenReturnNull() {
        // given
        String email = "nonexisting@example.com";
        String password = "password123";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when
        User loggedInUser = userService.login(email, password);

        // then
        assertNull(loggedInUser);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void whenResetPassword_withValidEmail_thenReturnTrue() {
        // given
        String email = "john.doe@example.com";
        String newPassword = "newPassword123";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        boolean result = userService.resetPassword(email, newPassword);

        // then
        assertTrue(result);
        assertEquals(newPassword, testUser.getPassword());
        assertEquals(0, testUser.getLoginAttempts());
        assertFalse(testUser.isLocked());
        assertNull(testUser.getLockUntil());
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void whenResetPassword_withNonExistingEmail_thenReturnFalse() {
        // given
        String email = "nonexisting@example.com";
        String newPassword = "newPassword123";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when
        boolean result = userService.resetPassword(email, newPassword);

        // then
        assertFalse(result);
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }
}