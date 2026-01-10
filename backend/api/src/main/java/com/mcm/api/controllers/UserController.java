package com.mcm.api.controllers;

import com.mcm.api.entities.UserAccount;
import com.mcm.api.repository.UserAccountRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserAccountRepository users;

    public UserController(UserAccountRepository users) {
        this.users = users;
    }

    @GetMapping
    public List<UserAccount> all() {
        return users.findAll();
    }

    @GetMapping("/{username}")
    public UserAccount byUsername(@PathVariable String username) {
        return users.findByUsernameIgnoreCase(username).orElseThrow();
    }
}
