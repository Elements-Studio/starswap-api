package org.starcoin.starswap.api.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.starcoin.starswap.api.data.model.LiquidityTokenFarmAccount;
import org.starcoin.starswap.api.data.model.LiquidityTokenFarmAccountId;

import java.util.List;

public interface LiquidityTokenFarmAccountRepository extends JpaRepository<LiquidityTokenFarmAccount, LiquidityTokenFarmAccountId> {

    List<LiquidityTokenFarmAccount> findByDeactivedIsFalse();

    List<LiquidityTokenFarmAccount> findByFarmAccountIdAccountAddress(String accountAddress);

}
