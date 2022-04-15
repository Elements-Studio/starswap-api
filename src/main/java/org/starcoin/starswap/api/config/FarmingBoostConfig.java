package org.starcoin.starswap.api.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class FarmingBoostConfig {

    @Bean
    public Map<String, String> farmingBoostWhitelist(@Value("${starswap.farming-boost.whitelist}") String json) {
        try {
            return new ObjectMapper().readValue(json, new TypeReference<Map<String, String>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Parse starswap.farming-boost.whitelist error.", e);
        }
    }
}
