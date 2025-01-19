package net.codejava.ProductManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl {

    @Autowired
    private UserRepository userRepo;

    public User loadUserByUsername(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Could not find user with email: " + email));
    }
}
