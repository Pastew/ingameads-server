package com.pastew.ingameadsui.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    private String password;

    private String email;

    private String[] roles;

    public User(String username, String password, String email, String... roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
    }
}
