package org.starcoin.starswap.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.starcoin.starswap.api.utils.AptosContractApiClient;
import org.starcoin.starswap.api.utils.ContractApiClient;

@Configuration
@ConditionalOnProperty(value = "aptos.enabled", havingValue = "true", matchIfMissing = false)
public class AptosChainConfig {

    @Bean
    public ContractApiClient aptosContractApiClient(@Value("${aptos.node-api.base-url}") String nodeApiBaseUrl,
                                                    @Value("${starswap.aptos-contract-address}") String contractAddress) {
        return new AptosContractApiClient(nodeApiBaseUrl, contractAddress);
    }

}
