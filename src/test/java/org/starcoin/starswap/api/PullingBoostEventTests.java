package org.starcoin.starswap.api;

import com.novi.serde.DeserializationError;
import org.junit.jupiter.api.Test;
import org.starcoin.bean.Event;
import org.starcoin.starswap.api.utils.JsonRpcClient;
import org.starcoin.starswap.types.BoostEvent;
import org.starcoin.utils.HexUtils;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.starcoin.starswap.api.data.model.PullingEventTask.PULLING_BLOCK_MAX_COUNT;

public class PullingBoostEventTests {

    @Test
    public void testPullBoostEvents() {
        BigInteger fromBlockNumber = new BigInteger("1146538");
        BigInteger maxToBlockNumber = new BigInteger("1146599");
        JsonRpcClient jsonRpcClient = null;
        try {
            jsonRpcClient = new JsonRpcClient("https://proxima-seed.starcoin.org");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        while (fromBlockNumber.compareTo(maxToBlockNumber) <= 0) {
            BigInteger toBlockNumber = fromBlockNumber.add(BigInteger.valueOf(PULLING_BLOCK_MAX_COUNT));
            if (toBlockNumber.compareTo(maxToBlockNumber) > 0) {
                toBlockNumber = maxToBlockNumber;
            }
//            if (LOG.isDebugEnabled()) {
//                LOG.debug("JSON RPC getting events... from block: " + fromBlockNumber + ", to block: " + toBlockNumber);
//            }
            System.out.println("JSON RPC getting events... from block: " + fromBlockNumber + ", to block: " + toBlockNumber);
            Map<String, Object> eventFilter = createEventFilterMap(fromBlockNumber, toBlockNumber);
            Event[] events = jsonRpcClient.getEvents(eventFilter);
            if (events == null) {
                break;
            }
            for (Event e : events) {
//                if (LOG.isDebugEnabled()) {
//                    LOG.debug("Processing a event: " + e);
//                }
                //System.out.println(e);
                try {
                    BoostEvent boostEvent = BoostEvent.bcsDeserialize(HexUtils.hexToByteArray(e.getData()));
                    System.out.println(HexUtils.byteListToHexWithPrefix(boostEvent.signer.value));
                } catch (DeserializationError ex) {
                    ex.printStackTrace();
                }
            }
            fromBlockNumber = toBlockNumber.add(BigInteger.ONE);
        }
    }

    private Map<String, Object> createEventFilterMap(BigInteger fromBlockNumber, BigInteger toBlockNumber) {
        Map<String, Object> eventFilter = new HashMap<>();
        // params: `from_block`, `to_block`, `event_keys`, `addrs`, `type_tags`, `limit`.
        eventFilter.put("addrs", Collections.singletonList("0x8c109349c6bd91411d6bc962e080c4a3"));
        eventFilter.put("type_tags", Arrays.asList("0x8c109349c6bd91411d6bc962e080c4a3::TokenSwapFarmBoost::BoostEvent"));
        //eventFilter.put("decode", true);
        eventFilter.put("from_block", fromBlockNumber);
        eventFilter.put("to_block", toBlockNumber);
        return eventFilter;
    }
}
