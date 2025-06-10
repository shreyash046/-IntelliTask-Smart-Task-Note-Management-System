package model;

import java.util.Objects;

public class User {
    private static long idCounter = 1;

    private final long id;
    private String name;
    private String email;
    private String password; // (⚠️ plaintext for now; we'll improve it later)
    private Role role;

    public enum Role {
        USER, ADMIN
    }

    public User(String name, String email, String password, Role role) {
        this.id = idCounter++; // You can replace with IdGenerator later
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id == user.id && email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
}
