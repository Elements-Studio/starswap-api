package org.starcoin.starswap.api.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.starcoin.starswap.api.data.model.LiquidityToken;
import org.starcoin.starswap.api.data.model.LiquidityTokenId;

import java.util.List;

public interface LiquidityTokenRepository extends JpaRepository<LiquidityToken, LiquidityTokenId> {

    List<LiquidityToken> findByDeactivedIsFalse();

    List<LiquidityToken> findByLiquidityTokenIdTokenXIdAndLiquidityTokenIdTokenYId(String x, String y);

//    Page<Token> findByNetworkAndDeletedAtIsNull(String network, Pageable page);

}
