package org.starcoin.starswap.api.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.starcoin.starswap.api.data.model.LiquidityAccount;
import org.starcoin.starswap.api.data.model.LiquidityAccountId;

import java.util.List;

public interface LiquidityAccountRepository extends JpaRepository<LiquidityAccount, LiquidityAccountId> {

    List<LiquidityAccount> findByDeactivedIsFalse();

    List<LiquidityAccount> findByLiquidityAccountIdAccountAddress(String accountAddress);

}
