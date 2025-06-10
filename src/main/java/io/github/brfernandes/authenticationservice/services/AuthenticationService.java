package io.github.brfernandes.authenticationservice.services;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.brfernandes.authenticationservice.dto.LoginUserDto;
import io.github.brfernandes.authenticationservice.dto.VerifyUserDto;
import io.github.brfernandes.authenticationservice.model.User;
import io.github.brfernandes.authenticationservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    // public User signup(RegisterUserDto input) {
    //     User user = new User(input.getUsername(), input.getEmail(), passwordEncoder.encode(input.getPassword()));
    //     user.setVerificationCode(generateVerificationCode());
    //     user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
    //     user.setEnabled(false);

    //     return userRepository.save(user);
    // }

    public LoginUserDto authenticate(LoginUserDto input) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getEmail(),
                            input.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException(e.getMessage());
        }
        return input;
    }


    public void verifyUser(VerifyUserDto input) {
        Optional<User> optionalUser = userRepository.findByEmail(input.getEmail());
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())){
                throw new RuntimeException("Verification code has expired");
            }
            if (user.getVerificationCode().equals(input.getVerificationCode())){
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpiresAt(null);
                userRepository.save(user);
            } else {
                throw new RuntimeException("Invalid verification code");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void resendVerificationCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public String generateVerificationCode() {
        Random random = new Random();
        int code  = random.nextInt(90000) + 10000;
        return String.valueOf(code);
    }
}
