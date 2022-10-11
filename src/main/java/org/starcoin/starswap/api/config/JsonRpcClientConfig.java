package org.starcoin.starswap.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.starcoin.starswap.api.utils.ContractApiClient;
import org.starcoin.starswap.api.utils.StarcoinContractApiClient;

import java.net.MalformedURLException;

@Configuration
public class JsonRpcClientConfig {

    @Bean
    @ConditionalOnProperty(value = "starcoin.enabled", havingValue = "true", matchIfMissing = true)
    public ContractApiClient starcoinContractApiClient(@Value("${starcoin.json-rpc-url}") String jsonRpcUrl) throws MalformedURLException {
        return new StarcoinContractApiClient(jsonRpcUrl);
    }

}
