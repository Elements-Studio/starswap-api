package org.starcoin.starswap.api.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.starcoin.starswap.api.data.model.StructType;
import org.starcoin.starswap.api.data.model.Token;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, String> {


    List<Token> findByDeactivedIsFalse();

    Token findFirstByTokenStructType(StructType structType);

    List<Token> findByScalingFactorIsNull();


}
