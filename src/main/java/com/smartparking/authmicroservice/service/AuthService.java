package com.smartparking.authmicroservice.service;

import com.smartparking.authmicroservice.dto.AuthResponse;
import com.smartparking.authmicroservice.dto.LoginRequest;
import com.smartparking.authmicroservice.dto.RegisterRequest;
import com.smartparking.authmicroservice.entity.User;
import com.smartparking.authmicroservice.entity.Role;
import com.smartparking.authmicroservice.repository.UserRepository;
import com.smartparking.authmicroservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Ky email eshte perdorur nje here per te hapur llogari");
        }

        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(
                        passwordEncoder.encode(request.password())
                )
                .role(Role.USER)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                user.getRole().name(),
                user.getEmail()
        );
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.email())
                .orElseThrow(
                        () -> new RuntimeException(
                                "Nje nga fushat e gabuar"
                        )
                );

        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword()
        )) {
            throw new RuntimeException(
                    "Nje nga fushat e gabuar"
            );
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                user.getRole().name(),
                user.getEmail()
        );
    }
}