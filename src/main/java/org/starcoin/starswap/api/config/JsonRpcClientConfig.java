package org.starcoin.starswap.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.starcoin.starswap.api.utils.JsonRpcClient;

import java.net.MalformedURLException;

@Configuration
public class JsonRpcClientConfig {

    @Bean
    public JsonRpcClient jsonRpcClient(@Value("${starcoin.json-rpc-url}") String jsonRpcUrl) throws MalformedURLException {
        return new JsonRpcClient(jsonRpcUrl);
    }

}
