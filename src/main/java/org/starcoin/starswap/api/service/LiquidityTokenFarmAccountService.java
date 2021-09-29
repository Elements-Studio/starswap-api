package org.starcoin.starswap.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.starcoin.starswap.api.data.model.LiquidityTokenFarmAccount;
import org.starcoin.starswap.api.data.model.LiquidityTokenFarmAccountId;
import org.starcoin.starswap.api.data.repo.LiquidityTokenFarmAccountRepository;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;

@Service
public class LiquidityTokenFarmAccountService {

    private static final Logger LOG = LoggerFactory.getLogger(LiquidityTokenFarmAccountService.class);

    @Autowired
    private LiquidityTokenFarmAccountRepository liquidityTokenFarmAccountRepository;


    public List<LiquidityTokenFarmAccount> findByAccountAddress(String accountAddress) {
        return liquidityTokenFarmAccountRepository.findByFarmAccountIdAccountAddress(accountAddress);
    }

    @Transactional
    public LiquidityTokenFarmAccount activeFarmAccount(LiquidityTokenFarmAccountId farmAccountId) {
        LiquidityTokenFarmAccount farmAccount = this.liquidityTokenFarmAccountRepository.findById(farmAccountId).orElse(null);
        if (farmAccount == null) {
            farmAccount = new LiquidityTokenFarmAccount();
            farmAccount.setFarmAccountId(farmAccountId);
            farmAccount.setStakeAmount(BigInteger.ZERO);
            farmAccount.setDeactived(false);
            farmAccount.setCreatedAt(System.currentTimeMillis());
            farmAccount.setCreatedBy("admin");
            farmAccount.setUpdatedAt(farmAccount.getCreatedAt());
            farmAccount.setUpdatedBy("admin");
        } else {
            farmAccount.setUpdatedAt(System.currentTimeMillis());
            farmAccount.setUpdatedBy("admin");
            farmAccount.setDeactived(false);
        }
        this.liquidityTokenFarmAccountRepository.save(farmAccount);
        return farmAccount;
    }

}
