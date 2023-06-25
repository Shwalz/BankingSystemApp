package models;


import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password;
    private UserType userType;

    public enum UserType {
        Client,
        Employee
    }

    public User(String username, String password, UserType userType) {
        this.username = username;
        this.password = password;
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public UserType getUserType() {
        return userType;
    }

    @Override
    public String toString() {
        return this.username;
    }
}


