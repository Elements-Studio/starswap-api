package org.starcoin.starswap.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.starcoin.starswap.api.data.model.PullingEventTask;
import org.starcoin.starswap.api.data.repo.PullingEventTaskRepository;
import org.starcoin.utils.BeanUtils2;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PullingEventTaskService {

    @Autowired
    private PullingEventTaskRepository pullingEventTaskRepository;

    @Transactional
    public void createOrUpdatePullingEventTask(PullingEventTask pullingEventTask) {
        if (pullingEventTask.getFromBlockNumber() == null) {
            throw new IllegalArgumentException("Argument 'fromBlockNumber' is null");
        }
        PullingEventTask targetEventTask = pullingEventTaskRepository.findById(pullingEventTask.getFromBlockNumber()).orElse(null);
        if (targetEventTask == null) {
            targetEventTask = new PullingEventTask();
            targetEventTask.setCreatedAt(System.currentTimeMillis());
            targetEventTask.setCreatedBy("ADMIN");
            targetEventTask.setUpdatedAt(targetEventTask.getCreatedAt());
            targetEventTask.setUpdatedBy(targetEventTask.getCreatedBy());
        } else {
            targetEventTask.setUpdatedAt(System.currentTimeMillis());
            targetEventTask.setUpdatedBy("ADMIN");
            targetEventTask.resetStatus();
        }
        Set<String> props = Arrays.stream(new String[]{"fromBlockNumber", "toBlockNumber"}).collect(Collectors.toSet());
        BeanUtils2.copySpecificProperties(pullingEventTask, targetEventTask, props);
        pullingEventTaskRepository.save(targetEventTask);
    }

    @Transactional
    public void createOrUpdatePullingEventTask(BigInteger fromBlockNumber, BigInteger toBlockNumber) {
        PullingEventTask targetEventTask = pullingEventTaskRepository.findById(fromBlockNumber).orElse(null);
        if (targetEventTask == null) {
            targetEventTask = new PullingEventTask();
            targetEventTask.setFromBlockNumber(fromBlockNumber);
            targetEventTask.setToBlockNumber(toBlockNumber);
            targetEventTask.setCreatedAt(System.currentTimeMillis());
            targetEventTask.setCreatedBy("ADMIN");
            targetEventTask.setUpdatedAt(targetEventTask.getCreatedAt());
            targetEventTask.setUpdatedBy(targetEventTask.getCreatedBy());
        } else {
            targetEventTask.setToBlockNumber(toBlockNumber);
            targetEventTask.setUpdatedAt(System.currentTimeMillis());
            targetEventTask.setUpdatedBy("ADMIN");
            targetEventTask.resetStatus();
        }
        pullingEventTaskRepository.save(targetEventTask);
    }

    @Transactional
    public void updateStatusDone(PullingEventTask t) {
        t.done();
        t.setUpdatedBy("ADMIN");
        t.setUpdatedAt(System.currentTimeMillis());
        pullingEventTaskRepository.save(t);
    }


    public List<PullingEventTask> getPullingEventTaskToProcess() {
        return pullingEventTaskRepository.findByStatusEquals(PullingEventTask.STATUS_CREATED);
    }

}
