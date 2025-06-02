package com.example.Metquay.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDto {
    private String name;
    private String email;
    private String password;
}