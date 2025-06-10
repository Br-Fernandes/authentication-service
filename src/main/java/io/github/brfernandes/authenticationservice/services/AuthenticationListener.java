package io.github.brfernandes.authenticationservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.brfernandes.authenticationservice.dto.LoginUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationListener {

    private final AuthenticationService authenticationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "new-jwt-topic", groupId = "authorization-user-consumer")
    public void createJwtToken(String message) throws JsonProcessingException {
        LoginUserDto user = objectMapper.readValue(message, LoginUserDto.class);
        User
        authenticationService.authenticate(user);
    }
}
