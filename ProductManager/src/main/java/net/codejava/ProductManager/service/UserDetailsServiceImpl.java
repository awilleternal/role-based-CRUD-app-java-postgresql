package net.codejava.ProductManager.service;

import net.codejava.ProductManager.entity.User;
import net.codejava.ProductManager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl {

    @Autowired
    private UserRepository userRepo;

    public User loadUserByUsername(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Could not find user with email: " + email));
    }
}
