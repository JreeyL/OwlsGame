package org.OwlsGame.backend.dao;

import org.OwlsGame.backend.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void whenFindByEmail_thenReturnUser() {
        // given
        User user = new User("John", "Doe", "password123", "john.doe@example.com");
        entityManager.persist(user);
        entityManager.flush();

        // when
        Optional<User> found = userRepository.findByEmail(user.getEmail());

        // then
        assertTrue(found.isPresent());
        assertEquals(user.getEmail(), found.get().getEmail());
        assertEquals(user.getFirstname(), found.get().getFirstname());
        assertEquals(user.getLastname(), found.get().getLastname());
    }

    @Test
    public void whenFindByNonExistingEmail_thenReturnEmpty() {
        // given
        String nonExistingEmail = "nonexisting@example.com";

        // when
        Optional<User> found = userRepository.findByEmail(nonExistingEmail);

        // then
        assertFalse(found.isPresent());
    }

    @Test
    public void whenSaveUser_thenUserIsPersisted() {
        // given
        User user = new User("Jane", "Smith", "password456", "jane.smith@example.com");

        // when
        User savedUser = userRepository.save(user);

        // then
        assertNotNull(savedUser.getId());
        assertEquals(user.getEmail(), savedUser.getEmail());
        assertEquals(user.getFirstname(), savedUser.getFirstname());
        assertEquals(user.getLastname(), savedUser.getLastname());
        assertFalse(savedUser.isLocked());
    }

    @Test
    public void whenUpdateUser_thenUserIsUpdated() {
        // given
        User user = new User("Alice", "Johnson", "password789", "alice.johnson@example.com");
        user = entityManager.persist(user);
        entityManager.flush();

        // when
        user.setFirstname("Alicia");
        user.setLocked(true);
        User updatedUser = userRepository.save(user);

        // then
        assertEquals("Alicia", updatedUser.getFirstname());
        assertTrue(updatedUser.isLocked());
    }

    @Test
    public void whenDeleteUser_thenUserIsRemoved() {
        // given
        User user = new User("Bob", "Williams", "passwordabc", "bob.williams@example.com");
        user = entityManager.persist(user);
        entityManager.flush();
        long userId = user.getId();

        // when
        userRepository.deleteById(userId);

        // then
        Optional<User> deletedUser = userRepository.findById(userId);
        assertFalse(deletedUser.isPresent());
    }

    @Test
    public void whenFindAll_thenReturnAllUsers() {
        // given
        User user1 = new User("User", "One", "pass1", "user1@example.com");
        User user2 = new User("User", "Two", "pass2", "user2@example.com");
        User user3 = new User("User", "Three", "pass3", "user3@example.com");

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);
        entityManager.flush();

        // when
        List<User> users = userRepository.findAll();

        // then
        assertEquals(3, users.size());
    }
}