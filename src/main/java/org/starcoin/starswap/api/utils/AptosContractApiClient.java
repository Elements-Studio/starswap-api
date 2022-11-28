package org.starcoin.starswap.api.utils;

import com.github.wubuku.aptos.bean.AccountResource;
import com.github.wubuku.aptos.bean.CoinInfo;
import com.github.wubuku.aptos.bean.Option;
import com.github.wubuku.aptos.bean.OptionalAggregator;
import com.github.wubuku.aptos.types.TypeInfo;
import com.github.wubuku.aptos.utils.NodeApiException;
import com.github.wubuku.aptos.utils.NodeApiUtils;
import com.github.wubuku.aptos.utils.StructTagUtils;
import com.novi.bcs.BcsDeserializer;
import com.novi.serde.DeserializationError;
import com.novi.serde.SerializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.starcoin.starswap.api.data.model.Pair;
import org.starcoin.starswap.api.data.model.Triple;
import org.starcoin.starswap.api.utils.bean.*;
import org.starcoin.starswap.api.vo.SyrupStakeVO;
import org.starcoin.utils.HexUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AptosContractApiClient implements ContractApiClient {
    private static final Logger LOG = LoggerFactory.getLogger(AptosContractApiClient.class);

    private static final long DEFAULT_OPERATION_NUMERATOR = 10;
    private static final long DEFAULT_OPERATION_DENUMERATOR = 60;
    private static final long DEFAULT_POUNDAGE_NUMERATOR = 3;
    private static final long DEFAULT_POUNDAGE_DENUMERATOR = 1000;
    private static final Integer HTTP_STATUS_NOT_FOUND = 404;
    private static BigInteger EXP_SCALE = new BigInteger("1000000000000000000"); //e18
    private final String nodeApiBaseUrl;
    private final String contractAddress;

    public AptosContractApiClient(String nodeApiBaseUrl, String contractAddress) {
        this.nodeApiBaseUrl = nodeApiBaseUrl;
        this.contractAddress = contractAddress;
    }

    public static int compareTokenCode(TypeInfo t1, TypeInfo t2) throws SerializationError {
        return ByteUtils.compareBytes(t1.bcsSerialize(), t2.bcsSerialize());
    }

    public static Pair<String, String> sortTokens(String tokenX, String tokenY) {
        StructTagUtils.StructTag tokenXStructTag = StructTagUtils.parseStructTag(tokenX);
        StructTagUtils.StructTag tokenYStructTag = StructTagUtils.parseStructTag(tokenY);
        TypeInfo tokenXTypeInfo = StructTagUtils.toTypeInfo(tokenXStructTag);
        TypeInfo tokenYTypeInfo = StructTagUtils.toTypeInfo(tokenYStructTag);
        try {
            int i = compareTokenCode(tokenXTypeInfo, tokenYTypeInfo);
            if (i < 0) {
                return new Pair<>(tokenX, tokenY);
            } else if (i > 0) {
                return new Pair<>(tokenY, tokenX);
            }
        } catch (SerializationError e) {
            throw new IllegalArgumentException(e);
        }
        throw new IllegalArgumentException("Two tokens are equal.");
    }

    private static SyrupStakeVO toSyrupStakeVO(SyrupStakeList.SyrupStake syrupStakeOnChain) {
        SyrupStakeVO t = new SyrupStakeVO();
        t.setId(syrupStakeOnChain.getId());
        t.setAmount(syrupStakeOnChain.getTokenAmount());
        t.setEndTime(syrupStakeOnChain.getEndTime());
        t.setStartTime(syrupStakeOnChain.getStartTime());
        t.setStepwiseMultiplier(syrupStakeOnChain.getStepwiseMultiplier().intValue());
        return t;
    }

    private String getVestarCoinType() {
        return contractAddress + "::VESTAR::VESTAR";
    }

    @Override
    public BigInteger tokenSwapFarmQueryTotalStake(String farmAddress, String lpTokenAddress, String tokenX, String tokenY) {
        //TokenSwapFarmScript::query_total_stake
        Pair<String, String> tp = sortTokens(tokenX, tokenY);
        String lpToken = getLiquidityTokenStructTag(tp);
        FarmingAssetExtend farmingAssetExtend = getFarmingAssetExtend("PoolTypeFarmPool", lpToken);
        return farmingAssetExtend != null ? new BigInteger(farmingAssetExtend.getAssetTotalAmount()) : BigInteger.ZERO;
    }

    @Override
    public BigInteger tokenSwapFarmQueryReleasePerSecondV2(String farmAddress, String lpTokenAddress, String tokenX, String tokenY) {
        //v1:
        //TokenSwapFarmScript::query_release_per_second
/*
            let farm_pool_info = borrow_global<FarmPoolInfo<X, Y>>(STAR::token_address());
            let (total_alloc_point, pool_release_per_second) = YieldFarming::query_global_pool_info<PoolTypeFarmPool>(STAR::token_address());
            pool_release_per_second * farm_pool_info.alloc_point / total_alloc_point
 */
        //TokenSwapFarmRouter::query_info_v2
        //TokenSwapFarmRouter::query_global_pool_info
        Pair<String, String> tp = sortTokens(tokenX, tokenY);
        String lpToken = getLiquidityTokenStructTag(tp);
        return tokenSwapFarmQueryReleasePerSecondV2(lpToken);
    }

    private BigInteger tokenSwapFarmQueryReleasePerSecondV2(String lpToken) {
        String poolType = "PoolTypeFarmPool";
        YieldFarmingGlobalPoolInfo yieldFarmingGlobalPoolInfo = getYieldFarmingGlobalPoolInfo(poolType);
        FarmingAssetExtend assetExtend = getFarmingAssetExtend(poolType, lpToken);
        BigInteger total_alloc_point = yieldFarmingGlobalPoolInfo.getTotalAllocPoint();
        BigInteger total_release_per_second = yieldFarmingGlobalPoolInfo.getPoolReleasePerSecond();
        BigInteger alloc_point = new BigInteger(assetExtend.getAllocPoint());
        return total_release_per_second.multiply(alloc_point).divide(total_alloc_point);
    }

    @Override
    public Pair<BigInteger, BigInteger> tokenSwapFarmQueryReleasePerSecondV2AndAssetTotalWeight(String farmAddress,
                                                                                                String lpTokenAddress,
                                                                                                String tokenX,
                                                                                                String tokenY) {
        Pair<String, String> tp = sortTokens(tokenX, tokenY);
        String lpToken = getLiquidityTokenStructTag(tp);
        BigInteger rps = tokenSwapFarmQueryReleasePerSecondV2(lpToken);
        FarmingAsset farmingAsset = getFarmingAsset("PoolTypeFarmPool", lpToken);
        BigInteger w = new BigInteger(farmingAsset.getAssetTotalWeight());
        return new Pair<>(rps, w);
    }

    @Override
    public Long tokenSwapFarmGetRewardMultiplier(String farmAddress, String lpTokenAddress,
                                                 String tokenX, String tokenY) {
        //TokenSwapFarmScript::get_farm_multiplier
//        let farm_pool_info = borrow_global<FarmPoolInfo<X, Y>>(STAR::token_address());
//        (farm_pool_info.alloc_point as u64)
        Pair<String, String> tp = sortTokens(tokenX, tokenY);
        //TokenSwapFarmScript::query_stake
        String farmPoolInfoResType = contractAddress + "::TokenSwapFarm::FarmPoolInfo<" + tp.getItem1() +
                ", " + tp.getItem2() + ">";
        AccountResource<FarmPoolInfo> farmPoolInfoResource;
        try {
            farmPoolInfoResource = NodeApiUtils.getAccountResource(this.nodeApiBaseUrl,
                    contractAddress, farmPoolInfoResType, FarmPoolInfo.class, null);
        } catch (NodeApiException nodeApiException) {
            if (HTTP_STATUS_NOT_FOUND.equals(nodeApiException.getHttpStatusCode())) {
                return 0L;//???
            } else {
                throw nodeApiException;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new BigInteger(farmPoolInfoResource.getData().getAllocPoint()).longValue();
    }

    @Override
    public Long tokenSwapFarmGetBoostFactor(String farmAddress, String lpTokenAddress,
                                            String tokenX, String tokenY, String accountAddress) {
        //TokenSwapFarmRouter::get_boost_factor
        FarmBoostUserInfo userInfo = getFarmBoostUserInfo(tokenX, tokenY, accountAddress);
        return userInfo != null ? userInfo.getBoostFactor() : 0L;
    }

    public BigInteger tokenSwapFarmGetAccountStakedLiquidity(String tokenX, String tokenY, String accountAddress) {
        return tokenSwapFarmGetAccountStakedLiquidity(contractAddress, tokenX, tokenY, accountAddress);
    }

    @Override
    public BigInteger tokenSwapFarmGetAccountStakedLiquidity(String farmAddress, String tokenX, String tokenY, String accountAddress) {
        Pair<String, String> tp = sortTokens(tokenX, tokenY);
        //TokenSwapFarmScript::query_stake
        try {
            //AccountResource<FarmPoolStake> farmPoolStakeResource = getFarmPoolStake(accountAddress, tp);
            FarmPoolStake farmPoolStake = getFarmPoolStake(accountAddress, tp);
            Long stakeId = farmPoolStake.getId();
            String stakeListExtResType = contractAddress + "::YieldFarmingV3::StakeListExtend<" +
                    contractAddress + "::TokenSwapGovPoolType::PoolTypeFarmPool, " +
                    getAptosCoinStructTag(getLiquidityTokenStructTag(tp)) +
                    ">";
            AccountResource<YieldFarmingV3StakeListExtend> stakeListExtRes = NodeApiUtils.getAccountResource(this.nodeApiBaseUrl,
                    accountAddress, stakeListExtResType, YieldFarmingV3StakeListExtend.class, null);
            YieldFarmingV3StakeExtend stakeExtend = stakeListExtRes.getData().getItems().stream()
                    .filter(i -> stakeId.equals(i.getId())).findFirst().orElse(null);
            return stakeExtend != null ? stakeExtend.getAssetAmount() : BigInteger.ZERO;
        } catch (NodeApiException nodeApiException) {
            if (HTTP_STATUS_NOT_FOUND.equals(nodeApiException.getHttpStatusCode())) {
                return BigInteger.ZERO;
            } else {
                throw nodeApiException;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BigInteger tokenSwapFarmLookupGain(String tokenX, String tokenY, String accountAddress) {
        Pair<String, String> tp = sortTokens(tokenX, tokenY);
        try {
            //AccountResource<FarmPoolStake> farmPoolStakeResource = getFarmPoolStake(accountAddress, tp);
            FarmPoolStake farmPoolStake = getFarmPoolStake(accountAddress, tp);
            Long stakeId = farmPoolStake.getId();
            String lpToken = getLiquidityTokenStructTag(tp);
            String poolType = "PoolTypeFarmPool";
            YieldFarmingV3StakeList farmingStakeList = getYieldFarmingStakeList(accountAddress, poolType, lpToken);
            YieldFarmingV3Stake yieldFarmingV3Stake = farmingStakeList.getItems().stream()
                    .filter(i -> stakeId.equals(i.getId())).findFirst().orElse(null);
            if (yieldFarmingV3Stake == null) {
                return BigInteger.ZERO;
            }
            FarmingAsset farmingAsset = getFarmingAsset(poolType, lpToken);
            FarmingAssetExtend farmingAssetExtend = getFarmingAssetExtend(poolType, lpToken);
            YieldFarmingGlobalPoolInfo globalPoolInfo = getYieldFarmingGlobalPoolInfo(poolType);
            // -----------------
            YieldFarmingHarvestCapability cap = farmPoolStake.getCap();
            BigInteger expectedGain = queryExpectGain(farmingAsset, farmingAssetExtend, globalPoolInfo, yieldFarmingV3Stake, cap);
            return expectedGain;
        } catch (NodeApiException nodeApiException) {
            if (HTTP_STATUS_NOT_FOUND.equals(nodeApiException.getHttpStatusCode())) {
                return BigInteger.ZERO;
            } else {
                throw nodeApiException;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private FarmPoolStake getFarmPoolStake(String accountAddress, Pair<String, String> tp) throws IOException {
        String farmPoolStakeResType = contractAddress + "::TokenSwapFarm::FarmPoolStake<" + tp.getItem1() +
                ", " + tp.getItem2() + ">";
        AccountResource<FarmPoolStake> farmPoolStakeResource = NodeApiUtils.getAccountResource(this.nodeApiBaseUrl,
                accountAddress, farmPoolStakeResType, FarmPoolStake.class, null);
        return farmPoolStakeResource.getData();
    }

    private String getLiquidityTokenStructTag(Pair<String, String> tp) {
        return contractAddress + "::TokenSwap::LiquidityToken<" + tp.getItem1() + ", " + tp.getItem2() + ">";
    }

    /**
     * @param token token type
     * @return "0x1::coin::Coin<" + token + ">"
     */
    private String getAptosCoinStructTag(String token) {
        return "0x1::coin::Coin<" + token + ">";
    }

    @Override
    public BigInteger syrupPoolQueryTotalStake(String poolAddress, String token) {
        FarmingAssetExtend farmingAssetExtend = getFarmingAssetExtend("PoolTypeSyrup", token);
        return new BigInteger(farmingAssetExtend.getAssetTotalAmount());
    }

//    private YieldFarmingGlobalPoolInfo syrupPoolQueryGlobalPoolInfo() {
//        return getYieldFarmingGlobalPoolInfo("PoolTypeSyrup");
//    }

    @Override
    public BigInteger syrupPoolQueryReleasePerSecondV2(String poolAddress, String token) {
        return getPoolReleasePerSecondV2("PoolTypeSyrup", token);
    }

    private BigInteger getPoolReleasePerSecondV2(String poolType, String token) {
        YieldFarmingGlobalPoolInfo syrupGlobalInfo = getYieldFarmingGlobalPoolInfo(poolType);
        BigInteger total_alloc_point = syrupGlobalInfo.getTotalAllocPoint();
        BigInteger total_release_per_second = syrupGlobalInfo.getPoolReleasePerSecond();

        FarmingAssetExtend assetExtend = getFarmingAssetExtend(poolType, token);
        BigInteger alloc_point = new BigInteger(assetExtend.getAllocPoint());
        return total_release_per_second.multiply(alloc_point).divide(total_alloc_point);
    }

    private FarmingAssetExtend getFarmingAssetExtend(String poolType, String token) {
        String extResourceType = contractAddress + "::YieldFarmingV3::FarmingAssetExtend<" +
                contractAddress + "::TokenSwapGovPoolType::" + poolType + ", " + getAptosCoinStructTag(token) + ">";
        AccountResource<FarmingAssetExtend> assetExtResource;
        try {
            assetExtResource = NodeApiUtils.getAccountResource(this.nodeApiBaseUrl,
                    contractAddress, extResourceType, FarmingAssetExtend.class, null);
//        } catch (NodeApiException nodeApiException) {
//            if (HTTP_STATUS_NOT_FOUND.equals(nodeApiException.getHttpStatusCode())) {
//                return null;
//            } else {
//                throw nodeApiException;
//            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FarmingAssetExtend assetExtend = assetExtResource.getData();
        return assetExtend;
    }

    private YieldFarmingGlobalPoolInfo getYieldFarmingGlobalPoolInfo(String poolType) {
        String resourceType = contractAddress + "::YieldFarmingV3::YieldFarmingGlobalPoolInfo<" +
                contractAddress + "::TokenSwapGovPoolType::" + poolType + ">";
        try {
            AccountResource<YieldFarmingGlobalPoolInfo> resource = NodeApiUtils.getAccountResource(this.nodeApiBaseUrl,
                    contractAddress, resourceType, YieldFarmingGlobalPoolInfo.class, null);
            return resource.getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private FarmingAsset getFarmingAsset(String poolType, String token) {
//        /*
//         * return (alloc_point, asset_total_amount, asset_total_weight, harvest_index).
//         */
//        List<BigInteger> resultFields = new ArrayList<>();
        try {
//            String extResourceType = contractAddress + "::YieldFarmingV3::FarmingAssetExtend<" +
//                    contractAddress + "::TokenSwapGovPoolType::PoolTypeSyrup" + ", " + getAptosCoinStructTag(token) + ">";
//            AccountResource<FarmingAssetExtend> extResource = NodeApiUtils.getAccountResource(this.nodeApiBaseUrl,
//                    contractAddress, extResourceType, FarmingAssetExtend.class, null);
//            resultFields.add(new BigInteger(extResource.getData().getAllocPoint()));
//            resultFields.add(new BigInteger(extResource.getData().getAssetTotalAmount()));

            String resourceType = contractAddress + "::YieldFarmingV3::FarmingAsset<" +
                    contractAddress + "::TokenSwapGovPoolType::" + poolType + ", " + getAptosCoinStructTag(token) + ">";
            AccountResource<FarmingAsset> resource = NodeApiUtils.getAccountResource(this.nodeApiBaseUrl,
                    contractAddress, resourceType, FarmingAsset.class, null);
//            resultFields.add(new BigInteger(resource.getData().getAssetTotalWeight()));
//            resultFields.add(new BigInteger(resource.getData().getHarvestIndex()));
            return resource.getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        return resultFields;
    }

    public List<SyrupStakeVO> syrupPoolQueryStakeList(String token, String accountAddress) {
        return syrupPoolQueryStakeList(contractAddress, token, accountAddress);
    }

    @Override
    public List<SyrupStakeVO> syrupPoolQueryStakeList(String poolAddress, String token, String accountAddress) {
        String poolType = "PoolTypeSyrup";
        YieldFarmingV3StakeList farmingStakeList = getYieldFarmingStakeList(accountAddress, poolType, token);
        SyrupStakeList syrupStakeList = getSyrupStakeList(accountAddress, token);
        List<SyrupStakeVO> stakeVOList = new ArrayList<>();
        for (YieldFarmingV3Stake s : farmingStakeList.getItems()) {
            SyrupStakeList.SyrupStake syrupStakeOnChain = syrupStakeList.getItems().stream()
                    .filter(i -> i.getId().equals(s.getId())).findFirst().orElse(null);
            if (syrupStakeOnChain == null) {
                throw new IllegalArgumentException("SyrupStakeList.SyrupStake not found in syrupStakeList.");
            }
            SyrupStakeVO t = toSyrupStakeVO(syrupStakeOnChain);
            t.setTokenTypeTag(token);
            stakeVOList.add(t);
        }
        return stakeVOList;
    }

    @Override
    public List<SyrupStakeVO> syrupPoolQueryStakeWithExpectedGainList(String poolAddress, String token, String accountAddress) {
        //TokenSwapSyrup::query_expect_gain
        SyrupStakeList syrupStakeList = getSyrupStakeList(accountAddress, token);
        String poolType = "PoolTypeSyrup";
        YieldFarmingV3StakeList farmingStakeList = getYieldFarmingStakeList(accountAddress, poolType, token);
        FarmingAsset farmingAsset = getFarmingAsset(poolType, token);
        FarmingAssetExtend farmingAssetExtend = getFarmingAssetExtend(poolType, token);
        YieldFarmingGlobalPoolInfo globalPoolInfo = getYieldFarmingGlobalPoolInfo(poolType);
        List<SyrupStakeVO> stakeVOList = new ArrayList<>();
        for (YieldFarmingV3Stake s : farmingStakeList.getItems()) {
            SyrupStakeList.SyrupStake syrupStakeOnChain = syrupStakeList.getItems().stream()
                    .filter(i -> i.getId().equals(s.getId())).findFirst().orElse(null);
            if (syrupStakeOnChain == null) {
                throw new IllegalArgumentException("SyrupStakeList.SyrupStake not found in syrupStakeList.");
            }
            SyrupStakeVO t = toSyrupStakeVO(syrupStakeOnChain);
            t.setTokenTypeTag(token);
            // -----------------
            YieldFarmingHarvestCapability cap = syrupStakeOnChain.getHarvestCap();
            BigInteger expectedGain = queryExpectGain(farmingAsset, farmingAssetExtend, globalPoolInfo, s, cap);
            t.setExpectedGain(expectedGain);
            // -----------------
            stakeVOList.add(t);
        }
        return stakeVOList;
    }

    /*
    public fun query_expect_gain<PoolType: store, RewardCoinT, AssetT: store>(
        user_addr: address,
        broker_addr: address,
        cap: &HarvestCapability<PoolType, AssetT>
    ): u128 acquires FarmingAsset, StakeList, FarmingAssetExtend, YieldFarmingGlobalPoolInfo {
        let farming_asset = borrow_global_mut<FarmingAsset<PoolType, AssetT>>(broker_addr);
        let stake_list = borrow_global_mut<StakeList<PoolType, AssetT>>(user_addr);
        // Start check
        let now_seconds = timestamp::now_seconds();
        assert!(now_seconds >= farming_asset.start_time, error::invalid_state(ERR_FARMING_NOT_READY));
     */

    private BigInteger queryExpectGain(FarmingAsset farmingAsset,
                                       FarmingAssetExtend farmingAssetExtend,
                                       YieldFarmingGlobalPoolInfo globalPoolInfo,
                                       YieldFarmingV3Stake yieldFarmingV3Stake,
                                       YieldFarmingHarvestCapability cap) {
        // ---------------------------------------
        // get gain info.
        // Start check
        long now_seconds = System.currentTimeMillis() / 1000;//timestamp::now_seconds();
        //assert!(now_seconds >= farming_asset.start_time, error::invalid_state(ERR_FARMING_NOT_READY));
        if (!(now_seconds >= farmingAsset.getStartTime())) {
            throw new IllegalArgumentException("ERR_FARMING_NOT_READY");//todo is this ok?
        }

        // Calculate from latest timestamp to deadline timestamp if deadline valid
        now_seconds = now_seconds > cap.getDeadline() ? now_seconds : cap.getDeadline();
            /*
            let farming_asset_extend = borrow_global<FarmingAssetExtend<PoolType, AssetT>>(broker_addr);
            // Calculate new harvest index
            let new_harvest_index = calculate_harvest_index_with_asset_v2<PoolType, AssetT>(
                farming_asset,
                farming_asset_extend,
                now_seconds
            );
            let new_gain = calculate_withdraw_amount_v2(new_harvest_index, stake.last_harvest_index, stake.asset_weight);
             */
        BigInteger new_harvest_index = calculateHarvestIndexWithAssetV2(farmingAsset, farmingAssetExtend,
                now_seconds, globalPoolInfo);
        BigInteger new_gain = calculateWithdrawAmountV2(new_harvest_index,
                new BigInteger(yieldFarmingV3Stake.getLastHarvestIndex()), new BigInteger(yieldFarmingV3Stake.getAssetWeight()));
        return new BigInteger(yieldFarmingV3Stake.getGain()).add(new_gain);
    }


    /// calculate pool harvest index
    /// harvest_index = (current_timestamp - last_timestamp) * pool_release_per_second * (alloc_point/total_alloc_point)  / (asset_total_weight );
    /// if farm:   asset_total_weight = Sigma (per user lp_amount *  user boost_factor)
    /// if stake:  asset_total_weight = Sigma (per user lp_amount *  stepwise_multiplier)
    private BigInteger calculateHarvestIndexWithAssetV2(
            FarmingAsset farming_asset,
            FarmingAssetExtend farming_asset_extend,
            Long now_seconds,
            YieldFarmingGlobalPoolInfo global_pool_info) {
/*
     fun calculate_harvest_index_with_asset_v2<PoolType: store, AssetT: store>(
        farming_asset: &FarmingAsset<PoolType, AssetT>,
        farming_asset_extend: &FarmingAssetExtend<PoolType, AssetT>,
        now_seconds: u64): u128  acquires YieldFarmingGlobalPoolInfo {
    }
 */
//        assert!(farming_asset.last_update_timestamp <= now_seconds, error::invalid_argument(ERR_FARMING_TIMESTAMP_INVALID));
        if (!(farming_asset.getLastUpdateTimestamp() <= now_seconds))
            throw new IllegalArgumentException("ERR_FARMING_TIMESTAMP_INVALID");
//        let golbal_pool_info = borrow_global<YieldFarmingGlobalPoolInfo<PoolType>>(@SwapAdmin);
//        let time_period = now_seconds - farming_asset.last_update_timestamp;
        Long time_period = now_seconds - farming_asset.getLastUpdateTimestamp();
//        let global_pool_reward = golbal_pool_info.pool_release_per_second * (time_period as u128);
        BigInteger global_pool_reward = global_pool_info.getPoolReleasePerSecond().multiply(BigInteger.valueOf(time_period));
//        let pool_reward = BigExponential::exp(global_pool_reward * farming_asset_extend.alloc_point, golbal_pool_info.total_alloc_point);
        BigInteger pool_reward = global_pool_reward.multiply(EXP_SCALE)
                .multiply(new BigInteger(farming_asset_extend.getAllocPoint())).divide(global_pool_info.getTotalAllocPoint());
//        // calculate period harvest index and global pool info when asset_total_weight is zero
//        let harvest_index_period = if (farming_asset.asset_total_weight <= 0) {
//            BigExponential::mantissa(pool_reward)
//        } else {
//            BigExponential::mantissa(BigExponential::div_exp(pool_reward, BigExponential::exp_direct(farming_asset.asset_total_weight)))
//        };
        BigInteger harvest_index_period;
        if (new BigInteger(farming_asset.getAssetTotalWeight()).compareTo(BigInteger.ZERO) <= 0) {
            harvest_index_period = pool_reward;
        } else {
            harvest_index_period = pool_reward.divide(new BigInteger(farming_asset.getAssetTotalWeight()));
        }
//        let index_accumulated = u256::add(
//                u256::from_u128(farming_asset.harvest_index),
//                harvest_index_period
//        );
//        BigExponential::to_safe_u128(index_accumulated)
        return new BigInteger(farming_asset.getHarvestIndex()).add(harvest_index_period);
    }

    /// calculate user gain index
    /// if farm:  gain = (current_index - last_index) * user_asset_weight; user_asset_weight = user_amount * boost_factor;
    /// if stake: gain = (current_index - last_index) * user_asset_weight; user_asset_weight = user_amount * stepwise_multiplier;
    private BigInteger calculateWithdrawAmountV2(BigInteger harvest_index,
                                                 BigInteger last_harvest_index,
                                                 BigInteger user_asset_weight) {
        //assert!(harvest_index >= last_harvest_index, error::invalid_argument(ERR_FARMING_CALC_LAST_IDX_BIGGER_THAN_NOW));
        if (!(harvest_index.compareTo(last_harvest_index) >= 0))
            throw new IllegalArgumentException("ERR_FARMING_CALC_LAST_IDX_BIGGER_THAN_NOW");
        BigInteger amount_u256 = user_asset_weight.multiply(harvest_index.subtract(last_harvest_index));
        //BigExponential::truncate(BigExponential::exp_from_u256(amount_u256))
        return amount_u256.divide(EXP_SCALE);
    }

    private SyrupStakeList getSyrupStakeList(String accountAddress, String token) {
        String syrupStakesResourceType = contractAddress + "::TokenSwapSyrup::SyrupStakeList<"
                + token //getAptosCoinStructTag(token)
                + ">";
        AccountResource<SyrupStakeList> syrupStakesResource = null;
        try {
            syrupStakesResource = NodeApiUtils.getAccountResource(this.nodeApiBaseUrl,
                    accountAddress, syrupStakesResourceType, SyrupStakeList.class, null);
        } catch (NodeApiException nodeApiException) {
            if (HTTP_STATUS_NOT_FOUND.equals(nodeApiException.getHttpStatusCode())) {
                SyrupStakeList emptySyrupStakeList = new SyrupStakeList();
                emptySyrupStakeList.setItems(Collections.emptyList());
                return emptySyrupStakeList;
            } else {
                throw nodeApiException;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SyrupStakeList syrupStakes = syrupStakesResource.getData();
        return syrupStakes;
    }

    private YieldFarmingV3StakeList getYieldFarmingStakeList(String accountAddress, String poolType, String token) {
        String listResourceType = contractAddress + "::YieldFarmingV3::StakeList<" +
                contractAddress + "::TokenSwapGovPoolType::" + poolType + ", " + getAptosCoinStructTag(token) + ">";
        AccountResource<YieldFarmingV3StakeList> stakeListResource;
        try {
            stakeListResource = NodeApiUtils.getAccountResource(this.nodeApiBaseUrl,
                    accountAddress, listResourceType, YieldFarmingV3StakeList.class, null);
        } catch (NodeApiException nodeApiException) {
            if (HTTP_STATUS_NOT_FOUND.equals(nodeApiException.getHttpStatusCode())) {
                YieldFarmingV3StakeList emptyStakeList = new YieldFarmingV3StakeList();
                emptyStakeList.setItems(Collections.emptyList());
                return emptyStakeList;
            } else {
                throw nodeApiException;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        YieldFarmingV3StakeList oStakeList = stakeListResource.getData();
        return oStakeList;
    }

    @Override
    public Triple<List<Long>, List<Long>, List<BigInteger>> syrupPoolQueryAllMultiplierPools(String poolAddress, String token) {
        //TokenSwapSyrup::query_all_multiplier_pools
        String poolType = "PoolTypeSyrup";
        List<Long> secs = new ArrayList<>();
        List<Long> multipliers = new ArrayList<>();
        List<BigInteger> amounts = new ArrayList<>();
        MultiplierPoolsGlobalInfo multiplierPoolsGlobalInfo = getMultiplierPoolsGlobalInfo(poolType, token);
        for (MultiplierPoolsGlobalInfo.MultiplierPool p : multiplierPoolsGlobalInfo.getItems()) {
            // Convert key to seconds
            BcsDeserializer deserializer = new BcsDeserializer(HexUtils.hexToByteArray(p.getKey()));
            try {
                secs.add(deserializer.deserialize_u64());
            } catch (DeserializationError e) {
                throw new RuntimeException(e);
            }
            multipliers.add(p.getMultiplier());
            amounts.add(new BigInteger(p.getAssetAmount()));
        }
        return new Triple<>(secs, multipliers, amounts);
//        BigInteger totalStake = syrupPoolQueryTotalStake(poolAddress, token);
////        Long[] secs = new Long[]{604800L, 1209600L, 2592000L, 5184000L, 7776000L};
////        Long[] multipliers = new Long[]{2L, 3L, 4L, 6L, 8L};
////        BigInteger[] amounts = new BigInteger[]{
////                totalStake.divide(BigInteger.valueOf(100)).multiply(BigInteger.valueOf(1L)),
////                totalStake.divide(BigInteger.valueOf(100)).multiply(BigInteger.valueOf(1L)),
////                totalStake.divide(BigInteger.valueOf(100)).multiply(BigInteger.valueOf(1L)),
////                totalStake.divide(BigInteger.valueOf(100)).multiply(BigInteger.valueOf(1L)),
////                totalStake.divide(BigInteger.valueOf(100)).multiply(BigInteger.valueOf(96L)),
////        };
//        //return new Triple<>(Arrays.asList(secs), Arrays.asList(multipliers), Arrays.asList(amounts));
//        // -------------- just return estimated values ... -----------------
//        SwapStepwiseMultiplierConfig config = getSwapStepwiseMultiplierConfig();
//        List<Long> secs = config.getList().stream().map(i -> i.getIntervalSec()).collect(Collectors.toList());
//        List<Long> multipliers = config.getList().stream().map(i -> i.getMultiplier()).collect(Collectors.toList());
//        Long maxMultiplier = multipliers.stream().max(Long::compareTo).orElse(1L);
//        List<BigInteger> amounts = new ArrayList<>();
//        for (int i = 0; i < multipliers.size(); i++) {
//            int p = 1;
//            if (maxMultiplier.equals(multipliers.get(i))) {
//                p = 100 - multipliers.size() + 1;
//            }
//            amounts.add(totalStake.divide(BigInteger.valueOf(100)).multiply(BigInteger.valueOf(p)));
//        }
//        // ----------------------------------------------------------------
//        return new Triple<>(secs, multipliers, amounts);

    }

    @Override
    public BigInteger getAccountVestarAmount(String accountAddress) {
        //query_vestar_amount
        String resourceType = contractAddress + "::TokenSwapVestarMinter::Treasury";
        try {
            AccountResource<TokenSwapVestarMinterTreasury> resource = NodeApiUtils.getAccountResource(this.nodeApiBaseUrl,
                    accountAddress, resourceType, TokenSwapVestarMinterTreasury.class, null);
            return resource.getData().getVtoken().getToken().getValue();
        } catch (NodeApiException nodeApiException) {
            if (HTTP_STATUS_NOT_FOUND.equals(nodeApiException.getHttpStatusCode())) {
                return BigInteger.ZERO;
            } else {
                throw nodeApiException;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BigInteger getLiquidityBalance(String tokenX, String tokenY, String accountAddress) {
        Pair<String, String> tp = sortTokens(tokenX, tokenY);
        String resourceType = "0x1::coin::CoinStore<" + getLiquidityTokenStructTag(tp) + " >";
        AccountResource<Map> accountResource;
        try {
            accountResource = NodeApiUtils.getAccountResource(this.nodeApiBaseUrl,
                    accountAddress, resourceType, Map.class, null);
        } catch (NodeApiException nodeApiException) {
            if (HTTP_STATUS_NOT_FOUND.equals(nodeApiException.getHttpStatusCode())) {
                return BigInteger.ZERO;
            } else {
                throw nodeApiException;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new BigInteger(((Map) accountResource.getData().get("coin")).get("value").toString());
    }

    @Override
    public BigInteger getVestarAmountByTokenTypeAndStakeId(String accountAddress, String token, Long stakeId) {
        //query_vestar_amount_by_staked_id
        //query_vestar_amount_by_staked_id_tokentype
        String resourceType = contractAddress + "::TokenSwapVestarMinter::MintRecordListT<" + token + ">"; // not aptos Coin!
        try {
            AccountResource<MintRecordListT> resource = NodeApiUtils.getAccountResource(this.nodeApiBaseUrl,
                    accountAddress, resourceType, MintRecordListT.class, null);
            TokenSwapVestarMinterMintRecordT recordT = resource.getData().getItems().stream()
                    .filter(i -> i.getId().equals(stakeId))
                    .findFirst().orElse(null);
            return recordT != null ? recordT.getMintedAmount() : BigInteger.ZERO;
        } catch (NodeApiException nodeApiException) {
            if (HTTP_STATUS_NOT_FOUND.equals(nodeApiException.getHttpStatusCode())) {
                return BigInteger.ZERO;
            } else {
                throw nodeApiException;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BigInteger tokenSwapRouterGetTotalLiquidity(String tokenX, String tokenY) {
        return tokenSwapRouterGetTotalLiquidity(contractAddress, tokenX, tokenY);
    }

    @Override
    public BigInteger tokenSwapRouterGetTotalLiquidity(String lpTokenAddress, String tokenX, String tokenY) {
        //TokenSwapRouter::total_liquidity
        Pair<String, String> tp = sortTokens(tokenX, tokenY);
        String lpToken = getLiquidityTokenStructTag(tp);
        return getCoinSupply(lpToken);
    }

    @Override
    public BigInteger getTokenScalingFactor(String token) {
        StructTagUtils.StructTag coinType = StructTagUtils.parseStructTag(token);
        String resourceType = "0x1::coin::CoinInfo<" + token + ">";
        try {
            AccountResource<Map> resource = NodeApiUtils.getAccountResource(this.nodeApiBaseUrl,
                    coinType.getAddress(), resourceType, Map.class, null);
            Integer d = Integer.valueOf(resource.getData().get("decimals").toString());
            return BigInteger.TEN.pow(d);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Pair<BigInteger, BigInteger> tokenSwapRouterGetReserves(String tokenX, String tokenY) {
        return tokenSwapRouterGetReserves(contractAddress, tokenX, tokenY);
    }

    @Override
    public Pair<BigInteger, BigInteger> tokenSwapRouterGetReserves(String lpTokenAddress, String tokenX, String tokenY) {
        Pair<String, String> tp = sortTokens(tokenX, tokenY);
        String resourceType = lpTokenAddress + "::TokenSwap::TokenSwapPair<" + tp.getItem1() + ", " + tp.getItem2() + ">";
        try {
            AccountResource<Map> resource = NodeApiUtils.getAccountResource(nodeApiBaseUrl,
                    lpTokenAddress,
                    resourceType, Map.class, null);
            Map r1 = (Map) resource.getData().get("token_x_reserve");
            Map r2 = (Map) resource.getData().get("token_y_reserve");
            BigInteger v1 = new BigInteger(r1.get("value").toString());
            BigInteger v2 = new BigInteger(r2.get("value").toString());
            return tokenX.equals(tp.getItem1()) ? new Pair<>(v1, v2) : new Pair<>(v2, v1);
        } catch (NodeApiException nodeApiException) {
            if (HTTP_STATUS_NOT_FOUND.equals(nodeApiException.getHttpStatusCode())) {
                LOG.info("tokenSwapRouterGetReserves, resource not found: " + resourceType);
                return new Pair<>(BigInteger.ZERO, BigInteger.ZERO);
            } else {
                throw nodeApiException;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BigInteger tokenSwapRouterGetAmountOut(String lpTokenAddress, BigInteger reserveIn, BigInteger reserveOut,
                                                  BigInteger amountIn,
                                                  long swapFeeNumerator, long swapFeeDenumerator) {
        if (!(amountIn.compareTo(BigInteger.ZERO) > 0))
            throw new IllegalArgumentException("ERROR_ROUTER_PARAMETER_INVALID");
        if (!(reserveIn.compareTo(BigInteger.ZERO) > 0 && reserveOut.compareTo(BigInteger.ZERO) > 0))
            throw new IllegalArgumentException("ERROR_ROUTER_PARAMETER_INVALID");
        if (!(swapFeeDenumerator > 0 && swapFeeNumerator > 0))
            throw new IllegalArgumentException("ERROR_SWAP_FEE_ALGORITHM_INVALID");
        if (!(swapFeeDenumerator > swapFeeNumerator))
            throw new IllegalArgumentException("ERROR_SWAP_FEE_ALGORITHM_INVALID");
        BigInteger amount_in_with_fee = amountIn.multiply(BigInteger.valueOf(swapFeeDenumerator - swapFeeNumerator));
        //let denominator = reserve_in * (fee_denumerator as u128) + amount_in_with_fee;
        BigInteger denominator = reserveIn.multiply(BigInteger.valueOf(swapFeeDenumerator)).add(amount_in_with_fee);
        return amount_in_with_fee.multiply(reserveOut).divide(denominator);
    }

    @Override
    public BigInteger tokenSwapRouterGetAmountIn(String lpTokenAddress, String tokenIn, String tokenOut,
                                                 BigInteger amountOut, long swapFeeNumerator, long swapFeeDenumerator) {
        Pair<BigInteger, BigInteger> reserves = tokenSwapRouterGetReserves(lpTokenAddress, tokenIn, tokenOut);
        BigInteger reserve_in = reserves.getItem1();
        BigInteger reserve_out = reserves.getItem2();
        if (!(amountOut.compareTo(BigInteger.ZERO) > 0))
            throw new IllegalArgumentException("ERROR_ROUTER_PARAMETER_INVALID");
        if (!(reserve_in.compareTo(BigInteger.ZERO) > 0 && reserve_out.compareTo(BigInteger.ZERO) > 0))
            throw new IllegalArgumentException("ERROR_ROUTER_PARAMETER_INVALID");
        if (!(swapFeeDenumerator > 0 && swapFeeNumerator > 0))
            throw new IllegalArgumentException("ERROR_SWAP_FEE_ALGORITHM_INVALID");
        if (!(swapFeeDenumerator > swapFeeNumerator))
            throw new IllegalArgumentException("ERROR_SWAP_FEE_ALGORITHM_INVALID");
        if (!(reserve_out.compareTo(amountOut) > 0))
            throw new IllegalArgumentException("ERROR_SWAP_FEE_ALGORITHM_INVALID");
        //let denominator = (reserve_out - amount_out) * ((fee_denumerator - fee_numerator) as u128);
        BigInteger denominator = (reserve_out.subtract(amountOut)).multiply(
                BigInteger.valueOf(swapFeeDenumerator - swapFeeNumerator));
        //let r = SafeMath::safe_mul_div_u128(amount_out * (fee_denumerator as u128), reserve_in, denominator);
        BigInteger r = amountOut.multiply(BigInteger.valueOf(swapFeeDenumerator))
                .multiply(reserve_in).divide(denominator);
        return r.add(BigInteger.ONE);//r + 1
        // ---------------------------------------------------------
        // r_in * r_out == new_r_in * new_r_out
        // r_in * r_out == (r_in + amount_in) * (r_out - amount_out)
        // r_in + amount_in == (r_in * r_out) / (r_out - amount_out)
        // amount_in = (r_in * r_out) / (r_out - amount_out) - r_in
        // amount_in = (r_in * r_out - r_in * (r_out - amount_out)) / (r_out - amount_out)
        // amount_in = r_in * amount_out / (r_out - amount_out)
        // ---------------------------------------------------------
    }

    @Override
    public Pair<Long, Long> tokenSwapRouterGetPoundageRate(String lpTokenAddress, String tokenX, String tokenY) {
        //todo ?
        return new Pair<>(DEFAULT_POUNDAGE_NUMERATOR, DEFAULT_POUNDAGE_DENUMERATOR);
    }

    @Override
    public Pair<Long, Long> tokenSwapRouterGetSwapFeeOperationRateV2(String lpTokenAddress, String tokenX, String tokenY) {
        //todo ?
        return new Pair<>(DEFAULT_OPERATION_NUMERATOR, DEFAULT_OPERATION_DENUMERATOR);
    }

    public BigInteger computeBoostFactor(BigInteger user_locked_vestar_amount,
                                         BigInteger user_locked_farm_amount,
                                         BigInteger total_farm_amount) {

        /// Boost multiplier calculation follows the formula
        /// @param The amount of Vestar staked by users
        /// @param The user's pledge amount on the current farm
        /// @param Total stake on the current farm
        /// return Boost factor Max:250
        /// `boost factor = ( UserLockedVeSTARAmount / TotalVeSTARAmount ) / ( ( 2 / 3) * UserLockedFarmAmount / TotalLockedFarmAmount ) + 1  `

//    public fun compute_boost_factor(user_locked_vestar_amount: u128,
//                                    user_locked_farm_amount: u128,
//                                    total_farm_amount: u128): u64 {
//        let factor = (math64::pow(10, 8) as u128);
        BigInteger factor = BigInteger.TEN.pow(8);
//        let total_vestar_amount = option::get_with_default(&coin::supply<VESTAR::VESTAR>(), 0u128);
        BigInteger total_vestar_amount = getCoinSupply(getVestarCoinType());
        if (total_vestar_amount == null) {
            total_vestar_amount = BigInteger.ZERO;
        }
//        let boost_factor = 1 * factor;
        BigInteger boost_factor = factor;
//        let dividend = SafeMath::mul_div(user_locked_vestar_amount, factor * 3, total_vestar_amount) * factor;
        BigInteger dividend = user_locked_vestar_amount.multiply(factor.multiply(BigInteger.valueOf(3))).divide(total_vestar_amount).multiply(factor);
//        let divisor  = SafeMath::mul_div(user_locked_farm_amount, factor * 2, total_farm_amount);
        BigInteger divisor = user_locked_farm_amount.multiply(factor.multiply(BigInteger.valueOf(2))).divide(total_farm_amount);
        if (divisor.compareTo(BigInteger.ZERO) != 0) {
            boost_factor = dividend.divide(divisor).add(boost_factor);
        }
//        if (boost_factor > (25 * factor / 10)) {
        if (boost_factor.compareTo(factor.multiply(BigInteger.valueOf(25)).divide(BigInteger.TEN)) > 0) {
//            boost_factor = 25 * factor / 10;
            boost_factor = factor.multiply(BigInteger.valueOf(25)).divide(BigInteger.TEN);
//        }else if( ( 1 * factor ) < boost_factor && boost_factor <  ( 1 * factor + 1 * ( factor / 100 ) ) ){
        } else if (factor.compareTo(boost_factor) < 0
                && boost_factor.compareTo(factor.add(factor.divide(BigInteger.valueOf(100)))) < 0) {
//            boost_factor =  1 * factor + 1 * ( factor / 100 ) ;
            boost_factor = factor.add(factor.divide(BigInteger.valueOf(100)));
        }
//        let boost_factor = boost_factor / ( factor / 100 );
//        return (boost_factor as u64)
        return boost_factor.divide(factor.divide(BigInteger.valueOf(100)));
    }

    public BigInteger getBoostLockedVestarAmount(String tokenX, String tokenY, String accountAddress) {
//    public fun get_boost_locked_vestar_amount<X: copy + drop + store, Y: copy + drop + store>(account: address): u128 acquires UserInfo {
//        if (exists<UserInfo<X, Y>>(account)) {
//            let user_info = borrow_global<UserInfo<X, Y>>(account);
//            let vestar_value = VToken::value<VESTAR>(&user_info.locked_vetoken);
//            vestar_value
//        } else {
//            0
//        }
        FarmBoostUserInfo userInfo = getFarmBoostUserInfo(tokenX, tokenY, accountAddress);
        return userInfo != null ? userInfo.getLockedVetoken().getToken().getValue() : BigInteger.ZERO;
    }

    /**
     * @return Return null if UserInfo resource is not found in accountAddress.
     */
    private FarmBoostUserInfo getFarmBoostUserInfo(String tokenX, String tokenY, String accountAddress) {
        Pair<String, String> tp = sortTokens(tokenX, tokenY);
        String resourceType = contractAddress + "::TokenSwapFarmBoost::UserInfo<" + tp.getItem1() + "," + tp.getItem2() + " >";
        FarmBoostUserInfo userInfo;
        try {
            AccountResource<FarmBoostUserInfo> resource = NodeApiUtils.getAccountResource(this.nodeApiBaseUrl,
                    accountAddress, resourceType, FarmBoostUserInfo.class, null);
            userInfo = resource.getData();
            return userInfo;
        } catch (NodeApiException nodeApiException) {
            if (HTTP_STATUS_NOT_FOUND.equals(nodeApiException.getHttpStatusCode())) {
                return null;
            } else {
                throw nodeApiException;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BigInteger getCoinSupply(String token) {
        StructTagUtils.StructTag coinType = StructTagUtils.parseStructTag(token);
        String resourceType = "0x1::coin::CoinInfo<" + token + ">";
        try {
            AccountResource<CoinInfo> resource = NodeApiUtils.getAccountResource(this.nodeApiBaseUrl,
                    coinType.getAddress(), resourceType, CoinInfo.class, null);
            Option<OptionalAggregator> s = resource.getData().getSupply();
            if (s == null || s.getVec() == null || s.getVec().size() == 0) {
                return null;
            }
            if (s.getVec().get(0).getAggregator() != null
                    && s.getVec().get(0).getInteger().getVec() != null
                    && s.getVec().get(0).getAggregator().getVec().size() > 0) {
                throw new IllegalArgumentException("Not supported token: " + token);
            }
            if (s.getVec().get(0).getInteger() != null
                    && s.getVec().get(0).getInteger().getVec() != null) {
                if (s.getVec().get(0).getInteger().getVec().size() == 1) {
                    return new BigInteger(s.getVec().get(0).getInteger().getVec().get(0).getValue());
                } else {
                    throw new RuntimeException("supply.getVec().get(0).getInteger().getVec().size() != 1");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private MultiplierPoolsGlobalInfo getMultiplierPoolsGlobalInfo(String poolType, String token) {
        String resourceType = contractAddress + "::TokenSwapSyrupMultiplierPool::MultiplierPoolsGlobalInfo<"
                + contractAddress + "::TokenSwapGovPoolType::" + poolType + ", " + getAptosCoinStructTag(token) + ">";
        AccountResource<MultiplierPoolsGlobalInfo> resource = null;
        try {
            resource = NodeApiUtils.getAccountResource(this.nodeApiBaseUrl,
                    contractAddress, resourceType, MultiplierPoolsGlobalInfo.class, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return resource.getData();
    }

//    private SwapStepwiseMultiplierConfig getSwapStepwiseMultiplierConfig() {
//        String resourceType = contractAddress + "::Config::Config<"
//                + contractAddress + "::TokenSwapConfig::SwapStepwiseMultiplierConfig" +
//                ">";
//        AccountResource<SwapAdminConfig.SwapStepwiseMultiplierConfig> resource = null;
//        try {
//            resource = NodeApiUtils.getAccountResource(this.nodeApiBaseUrl,
//                    contractAddress, resourceType, SwapAdminConfig.SwapStepwiseMultiplierConfig.class, null);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return resource.getData().getPayload();
//    }

}
