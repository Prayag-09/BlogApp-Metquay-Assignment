package com.example.Metquay.dtos;

import lombok.*;

/**
 * @author prayagtushar
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AuthResponse {
    private String token;
    private long expireIn;
}