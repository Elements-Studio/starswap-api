package org.starcoin.starswap.api.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.starcoin.starswap.api.data.model.LiquidityTokenFarm;
import org.starcoin.starswap.api.data.model.LiquidityTokenFarmId;

import java.util.List;

public interface LiquidityTokenFarmRepository extends JpaRepository<LiquidityTokenFarm, LiquidityTokenFarmId> {

    List<LiquidityTokenFarm> findByDeactivedIsFalse();

    @Query(value = "select * from liquidity_token_farm p where token_x_id = :tokenXId and token_y_id = :tokenYId", nativeQuery = true)
    List<LiquidityTokenFarm> findByLiquidityTokenFarmIdTokenXIdAndLiquidityTokenFarmIdTokenYId(String tokenXId, String tokenYId);

}
