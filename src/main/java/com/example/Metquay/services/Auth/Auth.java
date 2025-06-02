package com.example.Metquay.services.Auth;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author prayagtushar
 */


public interface Auth {
    UserDetails authenticate(String email, String password);
    String generateToken(UserDetails userDetails);
    UserDetails validateToken(String token);
}