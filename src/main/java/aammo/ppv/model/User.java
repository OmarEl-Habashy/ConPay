package aammo.ppv.model;

public class User {
    private int UserID;
    private String Username;
    private String email;
    private String Password;
    private String bio;
    private java.util.Date CreatedAt;

    public User(int UserID, String Username, String email, String bio) {
        this.UserID = UserID;
        this.Username = Username;
        this.email = email;
        this.bio = bio;
        this.CreatedAt = new java.util.Date();
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        this.UserID = userID;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        this.Username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
    public java.util.Date getCreatedAt() {
        return CreatedAt;
    }
    public void setCreatedAt(java.util.Date createdAt) {
        this.CreatedAt = createdAt;
    }
}
