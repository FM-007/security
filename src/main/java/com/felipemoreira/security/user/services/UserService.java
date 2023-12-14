package com.felipemoreira.security.user.services;

import com.felipemoreira.security.exceptions.UserAlreadyExistsException;
import com.felipemoreira.security.user.entities.User;
import com.felipemoreira.security.user.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private UserRepository userRepository;

    public User create(User user) {
        verifyExistsUser(user);

        user.setPassword(passwordEncoder().encode(user.getPassword()));
        User createUser = userRepository.save(user);

        return createUser;
    }

    private void verifyExistsUser(User user) {
        Optional<User> foundUser = userRepository.findByUsername(user.getUsername());

        if (foundUser.isPresent()) {
            throw new UserAlreadyExistsException(user.getUsername());
        }
    }
}
