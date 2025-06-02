package com.example.Metquay.services.User;

import com.example.Metquay.dtos.RegisterRequest;
import com.example.Metquay.model.User;

import java.util.UUID;

/**
 * @author prayagtushar
 */
public interface UserService {
    User getUserById(UUID id);
    User registerUser(RegisterRequest registerRequest);
}