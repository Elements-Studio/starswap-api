package org.starcoin.starswap.api.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.starcoin.starswap.api.data.model.LiquidityPool;
import org.starcoin.starswap.api.data.model.LiquidityPoolId;

import java.util.List;
import java.util.Map;

public interface LiquidityPoolRepository extends JpaRepository<LiquidityPool, LiquidityPoolId> {

    List<LiquidityPool> findByDeactivedIsFalse();

    @Query(value = "select * from liquidity_pool p where token_x_id = :tokenXId and token_y_id = :tokenYId", nativeQuery = true)
    List<LiquidityPool> findByLiquidityPoolIdTokenXIdAndLiquidityPoolIdTokenYId(String tokenXId, String tokenYId);

    @Query(value = "SELECT \n" +
            "    u.token_id,\n" +
            "    SUM(u.sum_reserve) AS total_reserve,\n" +
            "    SUM(u.sum_reserve_in_usd) AS total_reserve_in_usd\n" +
            "FROM\n" +
            "    (SELECT \n" +
            "        p.token_x_id AS token_id,\n" +
            "            SUM(p.token_x_reserve) AS sum_reserve,\n" +
            "            SUM(p.token_x_reserve_in_usd) AS sum_reserve_in_usd\n" +
            "    FROM\n" +
            "        liquidity_pool p\n" +
            "    GROUP BY p.token_x_id UNION SELECT \n" +
            "        p.token_y_id AS token_id,\n" +
            "            SUM(p.token_y_reserve) AS sum_reserve,\n" +
            "            SUM(p.token_y_reserve_in_usd) AS sum_reserve_in_usd\n" +
            "    FROM\n" +
            "        liquidity_pool p\n" +
            "    GROUP BY p.token_y_id) u\n" +
            "GROUP BY u.token_id;",
            nativeQuery = true)
    List<Map<String, Object>> sumReservesGroupByToken();
}
