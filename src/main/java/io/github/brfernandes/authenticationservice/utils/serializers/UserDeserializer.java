package io.github.brfernandes.authenticationservice.utils.serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.brfernandes.authenticationservice.dto.LoginUserDto;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class UserDeserializer implements Deserializer<LoginUserDto> {

    @Override
    public LoginUserDto deserialize(String topic, byte[] data) {
        try {
            return new ObjectMapper().readValue(data, LoginUserDto.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
