package org.starcoin.starswap.api.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.starcoin.starswap.api.data.model.Pair;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class TokenPairConfig {

    @Bean
    public List<Pair<String, String>> invisibleTokenPairs(@Value("${starswap.invisible-token-pairs}") String json) {
        try {
            List<String> tokenIdPairs = new ObjectMapper().readValue(json, new TypeReference<List<String>>() {
            });
            return tokenIdPairs.stream().map(pair -> {
                String[] tokens = pair.split(",");
                return new Pair<>(tokens[0].trim(), tokens[1].trim());
            }).collect(Collectors.toList());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Parse starswap.invisible-token-pairs error.", e);
        }
    }
}
