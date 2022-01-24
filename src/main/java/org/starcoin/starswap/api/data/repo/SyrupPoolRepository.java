package org.starcoin.starswap.api.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.starcoin.starswap.api.data.model.SyrupPool;
import org.starcoin.starswap.api.data.model.SyrupPoolId;

import java.util.List;

public interface SyrupPoolRepository extends JpaRepository<SyrupPool, SyrupPoolId> {

    List<SyrupPool> findByDeactivedIsFalse();

    @Query(value = "select * from syrup_pool p where token_id = :tokenId", nativeQuery = true)
    List<SyrupPool> findBySyrupPoolIdTokenId(String tokenId);

}
