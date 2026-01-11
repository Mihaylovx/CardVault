package com.mcm.api.services;

import com.mcm.api.entities.UserAccount;
import com.mcm.api.repository.UserAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserAccountRepository users;

    public UserService(UserAccountRepository users) {
        this.users = users;
    }

    public List<UserAccount> all() {
        return users.findAll();
    }

    public UserAccount byUsername(String username) {
        return users.findByUsernameIgnoreCase(username).orElseThrow();
    }
}
