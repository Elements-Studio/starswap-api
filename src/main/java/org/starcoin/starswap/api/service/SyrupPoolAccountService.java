package org.starcoin.starswap.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.starcoin.starswap.api.data.model.SyrupPoolAccount;
import org.starcoin.starswap.api.data.model.SyrupPoolAccountId;
import org.starcoin.starswap.api.data.repo.SyrupPoolAccountRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class SyrupPoolAccountService {

    private static final Logger LOG = LoggerFactory.getLogger(SyrupPoolAccountService.class);

    @Autowired
    private SyrupPoolAccountRepository syrupPoolAccountRepository;


    public List<SyrupPoolAccount> findByAccountAddress(String accountAddress) {
        return syrupPoolAccountRepository.findBySyrupPoolAccountIdAccountAddress(accountAddress);
    }

    @Transactional
    public SyrupPoolAccount activeFarmAccount(SyrupPoolAccountId poolAccountId) {
        SyrupPoolAccount farmAccount = this.syrupPoolAccountRepository.findById(poolAccountId).orElse(null);
        if (farmAccount == null) {
            farmAccount = new SyrupPoolAccount();
            farmAccount.setSyrupPoolAccountId(poolAccountId);
            //farmAccount.setStakeAmount(BigInteger.ZERO);
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
        this.syrupPoolAccountRepository.save(farmAccount);
        return farmAccount;
    }

}
