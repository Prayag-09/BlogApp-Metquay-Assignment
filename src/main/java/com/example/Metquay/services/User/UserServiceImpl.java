package com.example.Metquay.services.User;

import com.example.Metquay.dtos.RegisterRequest;
import com.example.Metquay.model.User;
import com.example.Metquay.repository.UserRepo;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author prayagtushar
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }

    @Override
    @Transactional
    public User registerUser(RegisterRequest registerRequest) {
        // Check if user with email already exists
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new EntityExistsException("User with email " + registerRequest.getEmail() + " already exists");
        }

        // Create new user
        User newUser = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build();

        userRepository.save(newUser);
        return newUser;
    }
}