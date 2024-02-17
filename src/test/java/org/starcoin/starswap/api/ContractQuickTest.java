package org.starcoin.starswap.api;

import org.starcoin.starswap.api.utils.ContractApiClient;
import org.starcoin.starswap.api.utils.StarcoinContractApiClient;
import org.starcoin.starswap.api.vo.AccountFarmStakeInfo;

import java.net.MalformedURLException;

public class ContractQuickTest {
    public static void main(String[] args) {
        String FARM_ADDRESS  = "0x8c109349c6bd91411d6bc962e080c4a3";
        String CONTRACT_ADDRESS = "0x8c109349c6bd91411d6bc962e080c4a3";
        String USER_ACCOUNT_ADDRESS = "0x8c109349c6bd91411d6bc962e080c4a3";
        try {
            ContractApiClient contractApiClient = new StarcoinContractApiClient("https://halley-seed.starcoin.org", CONTRACT_ADDRESS);
            AccountFarmStakeInfo accountFarmStakeInfo = contractApiClient.getAccountFarmStakeInfo(
                    FARM_ADDRESS,
                    CONTRACT_ADDRESS,
                    String.format("%s::STAR::STAR", CONTRACT_ADDRESS),
                    "0x00000000000000000000000000000001::STC::STC",
                    USER_ACCOUNT_ADDRESS);
            System.out.println(accountFarmStakeInfo);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
