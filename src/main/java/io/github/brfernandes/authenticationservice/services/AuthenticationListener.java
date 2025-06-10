package io.github.brfernandes.authenticationservice.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.brfernandes.authenticationservice.dto.LoginUserDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationListener {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "new-jwt-topic", groupId = "authorization-user-consumer")
    public void createJwtToken(String message) throws JsonProcessingException {
        LoginUserDto user = objectMapper.readValue(message, LoginUserDto.class);
        LoginUserDto authenticatedUser = authenticationService.authenticate(user);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        System.out.println(jwtToken);    
        //kafkaTemplate.send("new-jwt-topic", jwtToken);
    }
}
