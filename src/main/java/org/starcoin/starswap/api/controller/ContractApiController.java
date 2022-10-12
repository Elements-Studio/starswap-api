package org.starcoin.starswap.api.controller;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.starcoin.starswap.api.utils.ContractApiClient;

import javax.annotation.Resource;
import java.math.BigInteger;

@Api(tags = {"Starswap RESTful API"})
@RestController
@RequestMapping("v1/contract-api")
public class ContractApiController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContractApiController.class);

    @Resource
    private ContractApiClient contractApiClient;

    @GetMapping(path = "getTokenScalingFactor")
    public BigInteger getTokenScalingFactor(@RequestParam("token") String token) {
        return contractApiClient.getTokenScalingFactor(token);
    }

}
