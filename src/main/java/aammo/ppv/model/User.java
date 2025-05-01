package aammo.ppv.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    private static int idCounter = 0; // Static counter for auto-incrementing IDs
    private int userId;
    private String username;
    private String email;
    private String hashedPassword;
    private String bio;
    private LocalDateTime createdAt;

    public User(int userId, String username, String email, String hashedPassword, String bio, LocalDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.bio = bio;
        this.createdAt = createdAt;
    }
    public User( String username, String email, String hashedPassword, String bio, LocalDateTime createdAt) {
        this.userId = ++idCounter; // Increment the counter for each new user
        this.username = username;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.bio = bio;
        this.createdAt = createdAt;
    }
    public User(String username, String email, String hashedPassword) {
        this(username, email, hashedPassword, null, LocalDateTime.now());
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }
        this.email = email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        if (hashedPassword == null || hashedPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
        this.hashedPassword = hashedPassword;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", bio='" + bio + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return userId == user.userId &&
                Objects.equals(username, user.username) &&
                Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, email);
    }
}
