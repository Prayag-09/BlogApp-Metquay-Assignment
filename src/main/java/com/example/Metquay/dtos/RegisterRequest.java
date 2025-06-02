package com.example.Metquay.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author prayagtushar
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    @NotNull(message = "Name is required")
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between {min} and {max} characters")
    private String name;

    @NotNull(message = "Email is required")
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must be at most {max} characters")
    private String email;

    @NotNull(message = "Password is required")
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between {min} and {max} characters")
    private String password;
}