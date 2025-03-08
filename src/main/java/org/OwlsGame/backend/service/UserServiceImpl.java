package org.OwlsGame.backend.service;

import org.OwlsGame.backend.dao.UserRepository;
import org.OwlsGame.backend.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean validateCredentials(String email, String password) {
        // 通过email查找
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(u -> u.getPassword().equals(password)).orElse(false);
    }

    @Override
    public void lockAccount(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setLocked(true);
            userRepository.save(user);
        });
    }

    @Override
    public void unlockAccount(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setLocked(false);
            userRepository.save(user);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAccountLocked(String email) {
        return userRepository.findByEmail(email)
                .map(User::isLocked)
                .orElse(false);
    }
}