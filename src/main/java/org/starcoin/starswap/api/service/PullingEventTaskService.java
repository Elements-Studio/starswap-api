package org.starcoin.starswap.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.starcoin.starswap.api.data.model.PullingEventTask;
import org.starcoin.starswap.api.data.repo.PullingEventTaskRepository;
import org.starcoin.utils.BeanUtils2;

import javax.persistence.criteria.Predicate;
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
        } else if (PullingEventTask.STATUS_DONE.equalsIgnoreCase(targetEventTask.getStatus())
                && targetEventTask.getToBlockNumber().compareTo(pullingEventTask.getToBlockNumber()) < 0) {
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
        } else if (PullingEventTask.STATUS_DONE.equalsIgnoreCase(targetEventTask.getStatus())
                && targetEventTask.getToBlockNumber().compareTo(toBlockNumber) < 0) {
            targetEventTask.setToBlockNumber(toBlockNumber);
            targetEventTask.setUpdatedAt(System.currentTimeMillis());
            targetEventTask.setUpdatedBy("ADMIN");
            targetEventTask.resetStatus();
        }
        pullingEventTaskRepository.save(targetEventTask);
    }

    @Transactional
    public void updateStatusDone(BigInteger fromBlockNumber) {
        PullingEventTask t = pullingEventTaskRepository.findById(fromBlockNumber)
                .orElseThrow(() -> new IllegalArgumentException("PullingEventTask not found: " + fromBlockNumber));
        t.done();
        t.setUpdatedBy("ADMIN");
        t.setUpdatedAt(System.currentTimeMillis());
        pullingEventTaskRepository.save(t);
    }

    /**
     * Update processing info of pulling event task.
     *
     * @param fromBlockNumber task Id.
     */
    @Transactional
    public void updateProcessing(BigInteger fromBlockNumber) {
        PullingEventTask t = pullingEventTaskRepository.findById(fromBlockNumber)
                .orElseThrow(() -> new IllegalArgumentException("PullingEventTask not found: " + fromBlockNumber));
        if (!PullingEventTask.STATUS_PROCESSING.equalsIgnoreCase(t.getStatus())
                && !PullingEventTask.STATUS_CREATED.equalsIgnoreCase(t.getStatus())) {
            return;
        }
        if (PullingEventTask.STATUS_CREATED.equalsIgnoreCase(t.getStatus())) {
            t.processing(); // set status to processing
        }
        t.setUpdatedBy("ADMIN");
        t.setUpdatedAt(System.currentTimeMillis());
        pullingEventTaskRepository.save(t);
    }

    private static final long PROCESSING_TIMED_OUT_SECONDS = 120L;

    /**
     * Get first pulling event task to process.
     */
    @Transactional
    public PullingEventTask getPullingEventTaskToProcess() {
        Specification<PullingEventTask> specification = (root, query, cb) -> {
            Predicate statusIsCreated = cb.equal(root.get("status"), PullingEventTask.STATUS_CREATED);
            Predicate statusIsProcessing = cb.equal(root.get("status"), PullingEventTask.STATUS_PROCESSING);
            Predicate processingTimedOut = cb.lt(root.get("updatedAt"), System.currentTimeMillis() - PROCESSING_TIMED_OUT_SECONDS * 1000);
            return cb.or(statusIsCreated, cb.and(statusIsProcessing, processingTimedOut));
        };
        List<PullingEventTask> tasks = pullingEventTaskRepository.findAll(specification,
                PageRequest.of(0, 1, Sort.by("updatedAt").ascending())).getContent();
        if (tasks.size() == 0) {
            return null;
        }
        updateProcessing(tasks.get(0).getFromBlockNumber());
        return tasks.get(0);
    }

}
