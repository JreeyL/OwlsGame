package org.OwlsGame.backend.dao;

import org.OwlsGame.backend.models.User;
import java.sql.SQLException;
import java.util.List;

public interface UserDAO {
    void addUser(User user) throws SQLException;
    User getUserByEmail(String email) throws SQLException;
    List<User> getAllUsers() throws SQLException;
    void updateUser(User user) throws SQLException;
    void deleteUserByEmail(String email) throws SQLException;
    boolean validateUser(String username, String password) throws SQLException;
}