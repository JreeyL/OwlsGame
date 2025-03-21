package org.OwlsGame.backend.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        // 测试前初始化 User 对象
        user = new User();
        user.setId(1L);
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("Secure123!");
        user.setLocked(false);
    }

    //-------------------------
    // 基础属性测试
    //-------------------------
    @Test
    void testGettersAndSetters() {
        assertEquals(1L, user.getId());
        assertEquals("John", user.getFirstname());
        assertEquals("Doe", user.getLastname());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("Secure123!", user.getPassword());
        assertFalse(user.isLocked());
    }

    @Test
    void testLockUser() {
        // Given: 初始未锁定
        assertFalse(user.isLocked());

        // When: 执行锁定
        user.setLocked(true);

        // Then: 验证状态变化
        assertTrue(user.isLocked());
    }

    //-------------------------
    // 业务逻辑测试（示例）
    //-------------------------
    @Test
    void testFullNameConcatenation() {
        // 假设 User 类添加了 getFullName() 方法
        // user.setFirstname("John");
        // user.setLastname("Doe");
        // assertEquals("John Doe", user.getFullName());
    }

    @Test
    void testEmailFormatValidation() {
        // 假设 User 类添加了 isValidEmail() 方法
        // assertTrue(user.isValidEmail());
        // user.setEmail("invalid-email");
        // assertFalse(user.isValidEmail());
    }
}