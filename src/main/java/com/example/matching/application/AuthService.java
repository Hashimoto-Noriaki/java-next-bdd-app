package com.example.matching.application;

import com.example.matching.domain.User;
import com.example.matching.domain.exception.DuplicateEmailException;
import com.example.matching.domain.exception.InvalidCredentialsException;
import com.example.matching.infrastructure.UserRepository;
import com.example.matching.infrastructure.security.JwtUtil;
import com.example.matching.presentation.dto.LoginRequest;
import com.example.matching.presentation.dto.LoginResponse;
import com.example.matching.presentation.dto.RegisterRequest;
import com.example.matching.presentation.dto.RegisterResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }
        userRepository.save(new User(request.name(), request.email(), passwordEncoder.encode(request.password())));
        return new RegisterResponse("登録が完了しました");
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        return new LoginResponse(jwtUtil.generateToken(user.getEmail()));
    }
}
