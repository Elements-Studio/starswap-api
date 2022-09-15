package org.starcoin.starswap.api.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.starcoin.starswap.api.data.model.AptosEventHandle;
import org.starcoin.starswap.api.data.model.AptosEventHandleId;

public interface AptosEventHandleRepository extends JpaRepository<AptosEventHandle, AptosEventHandleId> {

    AptosEventHandle findFirstByEventJavaType(String eventJavaType);

}
