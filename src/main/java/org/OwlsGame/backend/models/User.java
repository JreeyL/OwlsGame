package org.OwlsGame.backend.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * User entity representing a user in the system.
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "first_name")
    private String firstname;

    @Column(name = "last_name")
    private String lastname;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(name = "is_locked", columnDefinition = "boolean default false")
    private boolean locked;

    @Column(name = "login_attempts", columnDefinition = "int default 0")
    private int loginAttempts;

    @Column(name = "lock_until")
    private LocalDateTime lockUntil; // The time until which the account is locked (UTC)

    // Constructors
    public User() {}

    public User(String firstname, String lastname, String password, String email) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.email = email;
        this.locked = false;
        this.loginAttempts = 0;
        this.lockUntil = null;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Whether the account is currently locked.
     */
    public boolean isLocked() {
        return locked;
    }
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getLoginAttempts() {
        return loginAttempts;
    }
    public void setLoginAttempts(int loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    /**
     * The LocalDateTime until which the account is locked (UTC recommended).
     */
    public LocalDateTime getLockUntil() {
        return lockUntil;
    }
    public void setLockUntil(LocalDateTime lockUntil) {
        this.lockUntil = lockUntil;
    }
}