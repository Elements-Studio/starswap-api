package org.starcoin.starswap.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StarcoinEventFilter {

    @Value("${starcoin.event-filter.from-address}")
    private String fromAddress;// = "0x598b8cbfd4536ecbe88aa1cfaffa7a62";
    @Value("${starcoin.event-filter.add-liquidity-event-type-tag}")
    private String addLiquidityEventTypeTag;// = "0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwap::AddLiquidityEvent";
    @Value("${starcoin.event-filter.add-farm-event-type-tag}")
    private String addFarmEventTypeTag;// = "0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapFarm::AddFarmEvent";
    @Value("${starcoin.event-filter.stake-event-type-tag}")
    private String stakeEventTypeTag;// = "0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapFarm::StakeEvent";
    @Value("${starcoin.event-filter.syrup-pool-stake-event-type-tag}")
    private String syrupPoolStakeEventTypeTag;

    public String getFromAddress() {
        return fromAddress;
    }

    public String getAddLiquidityEventTypeTag() {
        return addLiquidityEventTypeTag;
    }

    public String getAddFarmEventTypeTag() {
        return addFarmEventTypeTag;
    }

    public String getStakeEventTypeTag() {
        return stakeEventTypeTag;
    }

    public String getSyrupPoolStakeEventTypeTag() {
        return syrupPoolStakeEventTypeTag;
    }

    public String[] getEventTypeTags() {
        return new String[]{
                addLiquidityEventTypeTag,
                addFarmEventTypeTag,
                stakeEventTypeTag,
                syrupPoolStakeEventTypeTag
        };
    }
}
