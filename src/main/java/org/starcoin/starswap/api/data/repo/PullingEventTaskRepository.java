package org.starcoin.starswap.api.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.starcoin.starswap.api.data.model.PullingEventTask;

import java.math.BigInteger;
import java.util.List;

public interface PullingEventTaskRepository extends JpaRepository<PullingEventTask, BigInteger>, JpaSpecificationExecutor<PullingEventTask> {
    List<PullingEventTask> findByStatusEquals(String status);
}
