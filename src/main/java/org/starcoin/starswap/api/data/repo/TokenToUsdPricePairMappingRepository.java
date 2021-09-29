package org.starcoin.starswap.api.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.starcoin.starswap.api.data.model.TokenToUsdPricePairMapping;

public interface TokenToUsdPricePairMappingRepository extends JpaRepository<TokenToUsdPricePairMapping, String> {

}
