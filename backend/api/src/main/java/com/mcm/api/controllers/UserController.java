package com.mcm.api.controllers;

import com.mcm.api.entities.UserAccount;
import com.mcm.api.services.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserAccount> all() {
        return userService.all();
    }

    @GetMapping("/{username}")
    public UserAccount byUsername(@PathVariable String username) {
        return userService.byUsername(username);
    }
}
