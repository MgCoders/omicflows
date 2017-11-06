package coop.magnesium.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.dozd.mongo.annotation.Entity;
import eu.dozd.mongo.annotation.Id;

import java.io.Serializable;

/**
 * Created by rsperoni on 05/05/17.
 */
@Entity
public class User implements Serializable {

    @Id
    private String id;
    private String email;
    @JsonIgnore
    private String password;
    private String role = Role.USER.name();
    private String token;


    public User() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
