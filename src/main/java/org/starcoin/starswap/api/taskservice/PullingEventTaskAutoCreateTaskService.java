package org.starcoin.starswap.api.taskservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.starcoin.starswap.api.data.model.Pair;
import org.starcoin.starswap.api.service.NodeHeartbeatService;
import org.starcoin.starswap.api.service.PullingEventTaskService;

import java.math.BigInteger;
import java.util.List;

@Service
public class PullingEventTaskAutoCreateTaskService {
    private static final Logger LOG = LoggerFactory.getLogger(PullingEventTaskAutoCreateTaskService.class);

    private final PullingEventTaskService pullingEventTaskService;

    private final NodeHeartbeatService nodeHeartbeatService;

    public PullingEventTaskAutoCreateTaskService(
            @Autowired PullingEventTaskService pullingEventTaskService,
            @Autowired NodeHeartbeatService nodeHeartbeatService) {
        this.pullingEventTaskService = pullingEventTaskService;
        this.nodeHeartbeatService = nodeHeartbeatService;
    }

    @Scheduled(fixedDelayString = "${starswap.pulling-event-task-auto-add.fixed-delay}")
    public void task() {
        List<Pair<BigInteger, BigInteger>> breaks = nodeHeartbeatService.findBreakIntervals();
        for (Pair<BigInteger, BigInteger> b : breaks) {
            BigInteger fromBlockNumber = b.getItem1();
            BigInteger toBlockNumber = b.getItem2();
            pullingEventTaskService.createOrUpdatePullingEventTask(fromBlockNumber, toBlockNumber);
        }
    }


}
