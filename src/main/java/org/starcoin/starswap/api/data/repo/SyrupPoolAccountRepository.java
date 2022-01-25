package org.starcoin.starswap.api.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.starcoin.starswap.api.data.model.LiquidityTokenFarmAccount;
import org.starcoin.starswap.api.data.model.LiquidityTokenFarmAccountId;
import org.starcoin.starswap.api.data.model.SyrupPoolAccount;
import org.starcoin.starswap.api.data.model.SyrupPoolAccountId;

import java.util.List;

public interface SyrupPoolAccountRepository extends JpaRepository<SyrupPoolAccount, SyrupPoolAccountId> {

    List<SyrupPoolAccount> findByDeactivedIsFalse();

    List<SyrupPoolAccount> findBySyrupPoolAccountIdAccountAddress(String accountAddress);

}
