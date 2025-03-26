package org.OwlsGame.backend.dao;

import org.OwlsGame.backend.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        savedUser = new User();
        savedUser.setEmail("user@example.com");
        savedUser.setPassword("securePassword123");
        entityManager.persist(savedUser);
        entityManager.flush();
    }

    @Test
    void findByEmail_WhenUserExists_ShouldReturnUser() {
        // Act
        Optional<User> foundUser = userRepository.findByEmail("user@example.com");

        // Assert
        assertThat(foundUser)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getEmail()).isEqualTo("user@example.com");
                    assertThat(user.getPassword()).isEqualTo("securePassword123");
                });
    }

    @Test
    void findByEmail_WhenUserNotExists_ShouldReturnEmpty() {
        // Act
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertThat(foundUser).isEmpty();
    }

    @Test
    void findByEmail_ShouldBeCaseSensitive() {
        // Act
        Optional<User> foundLowercase = userRepository.findByEmail("user@example.com");
        Optional<User> foundUppercase = userRepository.findByEmail("USER@EXAMPLE.COM");

        // Assert
        assertThat(foundLowercase).isPresent();
        assertThat(foundUppercase).isEmpty(); // 默认区分大小写
    }

    @Test
    void findByEmail_WithDuplicateEmails_ShouldThrowException() {
        // Arrange
        User duplicateUser = new User();
        duplicateUser.setEmail("user@example.com"); // 重复邮箱
        duplicateUser.setPassword("anotherPassword");

        // Act & Assert
        assertThatThrownBy(() -> {
            entityManager.persist(duplicateUser);
            entityManager.flush(); // 显式触发约束检查
        })
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void findByEmail_WithNullEmail_ShouldReturnEmpty() {
        // Act
        Optional<User> foundUser = userRepository.findByEmail(null);

        // Assert
        assertThat(foundUser).isEmpty();
    }
}