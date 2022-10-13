package org.starcoin.starswap.api.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.novi.bcs.BcsDeserializer;
import com.novi.serde.DeserializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.starcoin.jsonrpc.client.JSONRPC2Session;
import org.starcoin.starswap.api.data.model.Pair;
import org.starcoin.starswap.api.data.model.SyrupStake;
import org.starcoin.starswap.api.data.model.Triple;
import org.starcoin.starswap.api.service.LiquidityPoolService;
import org.starcoin.utils.HexUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.starcoin.utils.JsonRpcClientUtils.contractCallV2;


public class JsonRpcUtils {

    private static final String SWAP_GET_AMOUNT_IN_OUT_FUNCTION_MODULE_NAME = "TokenSwapLibrary";
    private static final String SWAP_GET_RESERVES_FUNCTION_MODULE_NAME = "TokenSwapRouter";
    private static final String SWAP_FARM_QUERY_TOTAL_STAKE_FUNCTION_MODULE_NAME = "TokenSwapFarmScript";
    private static final String SWAP_FARM_QUERY_RELEASE_PER_SECOND_FUNCTION_MODULE_NAME = "TokenSwapFarmScript";
    private static final String TOKEN_SWAP_ROUTER_MODULE_NAME = "TokenSwapRouter";
    private static final String TOKEN_SWAP_FARM_SCRIPT_MODULE_NAME = "TokenSwapFarmScript";
    private static final String TOKEN_SWAP_SYRUP_MODULE_NAME = "TokenSwapSyrup";
    //private static final String TOKEN_SWAP_SYRUP_MULTIPLIER_POOL_MODULE_NAME = "TokenSwapSyrupMultiplierPool";
    private static final String TOKEN_SWAP_ORACLE_LIBRARY_MODULE_NAME = "TokenSwapOracleLibrary";
    private static final String TOKEN_SWAP_FARM_ROUTER_MODULE_NAME = "TokenSwapFarmRouter";
    //private static final String TOKEN_SWAP_SCRIPTS_MODULE_NAME = "TokenSwapScripts";
    private static final String TOKEN_SWAP_SYRUP_SCRIPT_MODULE_NAME = "TokenSwapSyrupScript";

    private static final String SWAP_FEE_NUMERATOR = "3u64";
    private static final String SWAP_FEE_DENUMERATOR = "1000u64";

    private static final Logger LOG = LoggerFactory.getLogger(LiquidityPoolService.class);

    public static Pair<BigInteger, BigInteger> getTokenSwapFarmStakedReserves(JSONRPC2Session jsonRpcSession, String farmAddress, String lpTokenAddress, String tokenX, String tokenY) {
        BigInteger stakedLiquidity = tokenSwapFarmQueryTotalStake(jsonRpcSession, farmAddress, tokenX, tokenY);
        return getReservesByLiquidity(jsonRpcSession, lpTokenAddress, tokenX, tokenY, stakedLiquidity);
    }

    public static Pair<BigInteger, BigInteger> getReservesByLiquidity(JSONRPC2Session jsonRpcSession, String lpTokenAddress, String tokenX, String tokenY, BigInteger liquidity) {
        Pair<BigInteger, BigInteger> totalReservesPair = tokenSwapRouterGetReserves(jsonRpcSession, lpTokenAddress, tokenX, tokenY);
        //System.out.println("totalReservesPair: " + totalReservesPair);
        BigInteger totalLiquidity = tokenSwapRouterTotalLiquidity(jsonRpcSession, lpTokenAddress, tokenX, tokenY);
        //System.out.println("totalLiquidity: " + totalLiquidity);
        return new Pair<>(
                totalReservesPair.getItem1().multiply(liquidity).divide(totalLiquidity),
                totalReservesPair.getItem2().multiply(liquidity).divide(totalLiquidity)
        );
    }

    // public fun total_liquidity<X: store, Y: store>(): u128
    public static BigInteger tokenSwapRouterTotalLiquidity(JSONRPC2Session jsonRpcSession, String lpTokenAddress, String tokenX, String tokenY) {
        List<String> resultFields = contractCallV2(jsonRpcSession, lpTokenAddress + "::"
                        + TOKEN_SWAP_ROUTER_MODULE_NAME + "::total_liquidity",
                Arrays.asList(tokenX, tokenY), Collections.emptyList(), new TypeReference<List<String>>() {
                });
        return new BigInteger(resultFields.get(0));
    }

    // public fun get_reserves<X: store, Y: store>(): (u128, u128)
    public static Pair<BigInteger, BigInteger> tokenSwapRouterGetReserves(JSONRPC2Session jsonRpcSession, String lpTokenAddress, String tokenX, String tokenY) {
        List<Object> resultFields = contractCallV2(jsonRpcSession, lpTokenAddress + "::"
                        + SWAP_GET_RESERVES_FUNCTION_MODULE_NAME + "::get_reserves",
                Arrays.asList(tokenX, tokenY), Collections.emptyList(), new TypeReference<List<Object>>() {
                });
        return new Pair<>(new BigInteger(resultFields.get(0).toString()), new BigInteger(resultFields.get(1).toString()));
    }

    // public fun get_amount_out(amount_in: u128, reserve_in: u128, reserve_out: u128): u128
    public static BigInteger tokenSwapRouterGetAmountOut(JSONRPC2Session jsonRpcSession, String lpTokenAddress, BigInteger amountIn, BigInteger reserveIn, BigInteger reserveOut) {
        List<String> resultFields = contractCallV2(jsonRpcSession, lpTokenAddress + "::"
                        + SWAP_GET_AMOUNT_IN_OUT_FUNCTION_MODULE_NAME + "::get_amount_out",
                Collections.emptyList(), Arrays.asList(
                        amountIn.toString() + "u128",
                        reserveIn.toString() + "u128",
                        reserveOut.toString() + "u128",
                        SWAP_FEE_NUMERATOR, SWAP_FEE_DENUMERATOR
                ), new TypeReference<List<String>>() {
                });
        return new BigInteger(resultFields.get(0));
    }

    public static BigInteger tokenSwapRouterGetAmountIn(JSONRPC2Session jsonRpcSession, String lpTokenAddress, BigInteger amountOut, BigInteger reserveIn, BigInteger reserveOut) {
        List<String> resultFields = contractCallV2(jsonRpcSession, lpTokenAddress + "::"
                        + SWAP_GET_AMOUNT_IN_OUT_FUNCTION_MODULE_NAME + "::get_amount_in",
                Collections.emptyList(), Arrays.asList(
                        amountOut.toString() + "u128",
                        reserveIn.toString() + "u128",
                        reserveOut.toString() + "u128",
                        SWAP_FEE_NUMERATOR, SWAP_FEE_DENUMERATOR
                ), new TypeReference<List<String>>() {
                });
        return new BigInteger(resultFields.get(0));
    }

    public static Pair<Long, Long> tokenSwapRouterGetPoundageRate(JSONRPC2Session jsonRpcSession, String lpTokenAddress, String tokenX, String tokenY) {
        List<Object> resultFields = contractCallV2(jsonRpcSession, lpTokenAddress + "::"
                        + TOKEN_SWAP_ROUTER_MODULE_NAME + "::get_poundage_rate",
                Arrays.asList(tokenX, tokenY), Collections.emptyList(), new TypeReference<List<Object>>() {
                });
        return new Pair<>(Long.parseLong(resultFields.get(0).toString()), Long.parseLong(resultFields.get(1).toString()));
    }

    public static Pair<Long, Long> tokenSwapRouterGetSwapFeeOperationRateV2(JSONRPC2Session jsonRpcSession, String lpTokenAddress, String tokenX, String tokenY) {
        List<Object> resultFields = contractCallV2(jsonRpcSession, lpTokenAddress + "::"
                        + TOKEN_SWAP_ROUTER_MODULE_NAME + "::get_swap_fee_operation_rate_v2",
                Arrays.asList(tokenX, tokenY), Collections.emptyList(), new TypeReference<List<Object>>() {
                });
        return new Pair<>(Long.parseLong(resultFields.get(0).toString()), Long.parseLong(resultFields.get(1).toString()));
    }

    //public fun query_total_stake<TokenX: store, TokenY: store>() : u128
    public static BigInteger tokenSwapFarmQueryTotalStake(JSONRPC2Session jsonRpcSession, String farmAddress, String tokenX, String tokenY) {
        List<String> resultFields = contractCallV2(jsonRpcSession, farmAddress + "::"
                        + SWAP_FARM_QUERY_TOTAL_STAKE_FUNCTION_MODULE_NAME + "::query_total_stake",
                Arrays.asList(tokenX, tokenY), Collections.emptyList(), new TypeReference<List<String>>() {
                });
        return new BigInteger(resultFields.get(0));
    }

    public static BigInteger tokenSwapFarmQueryReleasePerSecond(JSONRPC2Session jsonRpcSession, String farmAddress, String tokenX, String tokenY) {
        List<String> resultFields = contractCallV2(jsonRpcSession, farmAddress + "::"
                        + SWAP_FARM_QUERY_RELEASE_PER_SECOND_FUNCTION_MODULE_NAME + "::query_release_per_second",
                Arrays.asList(tokenX, tokenY), Collections.emptyList(), new TypeReference<List<String>>() {
                });
        return new BigInteger(resultFields.get(0));
    }

    public static Integer tokenSwapFarmGetRewardMultiplier(JSONRPC2Session jsonRpcSession, String farmAddress, String tokenX, String tokenY) {
        List<Object> resultFields = contractCallV2(jsonRpcSession, farmAddress + "::"
                        + TOKEN_SWAP_FARM_SCRIPT_MODULE_NAME + "::get_farm_multiplier",//
                Arrays.asList(tokenX, tokenY), Collections.emptyList(), new TypeReference<List<Object>>() {
                });
        return Integer.parseInt(resultFields.get(0).toString());
    }

    public static BigInteger tokenSwapFarmGetAccountStakedLiquidity(JSONRPC2Session jsonRpcSession, String farmAddress, String tokenX, String tokenY, String accountAddress) {
        List<Object> resultFields = contractCallV2(jsonRpcSession, farmAddress + "::"
                        + TOKEN_SWAP_FARM_SCRIPT_MODULE_NAME + "::query_stake",//
                Arrays.asList(tokenX, tokenY), Collections.singletonList(accountAddress), new TypeReference<List<Object>>() {
                });
        return new BigInteger(resultFields.get(0).toString());
    }

    /***
     * TokenSwapFarmRouter::query_global_pool_info
     * @param jsonRpcSession
     * @param farmAddress
     * @return total_alloc_point, pool_release_per_second.
     */
    public static Pair<BigInteger, BigInteger> tokenSwapFarmQueryGlobalPoolInfo(JSONRPC2Session jsonRpcSession, String farmAddress) {
        List<Object> resultFields = contractCallV2(jsonRpcSession, farmAddress + "::"
                        + TOKEN_SWAP_FARM_ROUTER_MODULE_NAME + "::query_global_pool_info",//
                Collections.emptyList(), Collections.emptyList(), new TypeReference<List<Object>>() {
                });
        return new Pair<>(new BigInteger(resultFields.get(0).toString()), new BigInteger(resultFields.get(1).toString()));
    }

    /***
     * TokenSwapFarmRouter::query_info_v2
     * @param jsonRpcSession
     * @param farmAddress
     * @param tokenX
     * @param tokenY
     * @return (alloc_point, asset_total_amount, asset_total_weight, harvest_index)
     */
    public static List<BigInteger> tokenSwapFarmQueryInfoV2(JSONRPC2Session jsonRpcSession, String farmAddress, String tokenX, String tokenY) {
        List<Object> resultFields = contractCallV2(jsonRpcSession, farmAddress + "::"
                        + TOKEN_SWAP_FARM_ROUTER_MODULE_NAME + "::query_info_v2",//
                Arrays.asList(tokenX, tokenY), Collections.emptyList(), new TypeReference<List<Object>>() {
                });
        return Arrays.asList(new BigInteger(resultFields.get(0).toString()),
                new BigInteger(resultFields.get(1).toString()),
                new BigInteger(resultFields.get(2).toString()),
                new BigInteger(resultFields.get(3).toString()));
    }

    public static Long tokenSwapFarmGetBoostFactor(JSONRPC2Session jsonRpcSession, String farmAddress, String tokenX, String tokenY, String accountAddress) {
        //public fun get_boost_factor<X: copy + drop + store, Y: copy + drop + store>(account: address): u64 {
        List<Object> resultFields = contractCallV2(jsonRpcSession, farmAddress + "::"
                        + TOKEN_SWAP_FARM_ROUTER_MODULE_NAME + "::get_boost_factor",//
                Arrays.asList(tokenX, tokenY), Collections.singletonList(accountAddress), new TypeReference<List<Object>>() {
                });
        return Long.parseLong(resultFields.get(0).toString());
    }

    // ------------------------

    public static BigInteger syrupPoolQueryTotalStake(JSONRPC2Session jsonRpcSession, String poolAddress, String token) {
        // the 'syrup_pool' fun is deleted!
//        List<String> resultFields = contractCallV2(jsonRpcSession, poolAddress + "::"
//                        + TOKEN_SWAP_SYRUP_MODULE_NAME + "::syrup_pool",
//                Collections.singletonList(token), Collections.emptyList(), new TypeReference<List<String>>() {
//                });
//        return new BigInteger(resultFields.get(0));
        List<BigInteger> info = syrupPoolQueryPoolInfoV2(jsonRpcSession, poolAddress, token);
        return info.get(1);
    }

//    public static BigInteger syrupPoolQueryReleasePerSecond(JSONRPC2Session jsonRpcSession, String poolAddress, String token) {
//        List<String> resultFields = contractCallV2(jsonRpcSession, poolAddress + "::"
//                        + TOKEN_SWAP_SYRUP_MODULE_NAME + "::query_release_per_second",
//                Collections.singletonList(token), Collections.emptyList(), new TypeReference<List<String>>() {
//                });
//        return new BigInteger(resultFields.get(0));
//    }


    public static List<Long> syrupPoolQueryStakeList(JSONRPC2Session jsonRpcSession, String poolAddress, String token, String accountAddress) {
        List<List<Long>> resultFields = contractCallV2(jsonRpcSession, poolAddress + "::"
                        + TOKEN_SWAP_SYRUP_MODULE_NAME + "::query_stake_list",//
                Collections.singletonList(token), Collections.singletonList(accountAddress), new TypeReference<List<List<Long>>>() {
                });
        if (resultFields.size() > 0) {
            return resultFields.get(0);
        } else {
            return Collections.emptyList();
        }
    }

    public static SyrupStake syrupPoolGetStakeInfo(JSONRPC2Session jsonRpcSession, String poolAddress, String token, String accountAddress, Long id) {
        //public fun get_stake_info<TokenT: store>(user_addr: address, id: u64): (u64, u64, u64, u128) acquires SyrupStakeList {
        ////...
        //(
        //        stake.start_time,
        //        stake.end_time,
        //        stake.stepwise_multiplier,
        //        stake.token_amount
        //)
        List<Object> resultFields = contractCallV2(jsonRpcSession, poolAddress + "::"
                        + TOKEN_SWAP_SYRUP_MODULE_NAME + "::get_stake_info",//
                Collections.singletonList(token), Arrays.asList(accountAddress, id.toString() + "u64"), new TypeReference<List<Object>>() {
                });
        SyrupStake syrupStake = new SyrupStake();
        syrupStake.setId(id);
        syrupStake.setStartTime(Long.parseLong(resultFields.get(0).toString()));
        syrupStake.setEndTime(Long.parseLong(resultFields.get(1).toString()));
        syrupStake.setStepwiseMultiplier(Integer.parseInt(resultFields.get(2).toString()));
        syrupStake.setAmount(new BigInteger(resultFields.get(3).toString()));
        return syrupStake;
    }

    public static BigInteger syrupPoolQueryExpectedGain(JSONRPC2Session jsonRpcSession, String poolAddress, String token, String accountAddress, Long id) {
        //public fun query_expect_gain<TokenT: store>(user_addr: address, id: u64): u128
        List<BigInteger> resultFields = contractCallV2(jsonRpcSession, poolAddress + "::"
                        + TOKEN_SWAP_SYRUP_MODULE_NAME + "::query_expect_gain",//
                Collections.singletonList(token),
                Arrays.asList(accountAddress, id.toString() + "u64"),
                new TypeReference<List<BigInteger>>() {
                });
        if (resultFields.size() > 0) {
            return resultFields.get(0);
        } else {
            return null;
        }
    }

    public static BigInteger syrupPoolQueryReleasePerSecondV2(JSONRPC2Session jsonRpcSession, String poolAddress, String token) {
        List<BigInteger> syrupTotalInfo = syrupPoolQuerySyrupInfo(jsonRpcSession, poolAddress);
        BigInteger total_alloc_point = syrupTotalInfo.get(0);
        BigInteger pool_release_per_second = syrupTotalInfo.get(1);
        List<BigInteger> poolInfo = syrupPoolQueryPoolInfoV2(jsonRpcSession, poolAddress, token);
        BigInteger alloc_point = poolInfo.get(0);
        return pool_release_per_second.multiply(alloc_point).divide(total_alloc_point);
    }

    /**
     * @return (total_alloc_point, pool_release_per_second)
     */
    public static List<BigInteger> syrupPoolQuerySyrupInfo(JSONRPC2Session jsonRpcSession,
                                                           String poolAddress) {
        /*
        /// Queyry global pool info
        /// return value: (total_alloc_point, pool_release_per_second)
        public fun query_syrup_info(): (u128, u128) {
         */
        List<BigInteger> resultFields = contractCallV2(jsonRpcSession, poolAddress + "::"
                        + TOKEN_SWAP_SYRUP_MODULE_NAME + "::query_syrup_info",//
                Collections.emptyList(),
                Collections.emptyList(),
                new TypeReference<List<BigInteger>>() {
                });
        return resultFields;
    }

    /**
     * @return (alloc_point, asset_total_amount, asset_total_weight, harvest_index)
     */
    public static List<BigInteger> syrupPoolQueryPoolInfoV2(JSONRPC2Session jsonRpcSession,
                                                            String poolAddress,
                                                            String token) {
        /*
        /// Query pool info from pool type v2
        /// return value: (alloc_point, asset_total_amount, asset_total_weight, harvest_index)
        public fun query_pool_info_v2<TokenT: store>(): (u128, u128, u128, u128) {
         */
        List<BigInteger> resultFields = contractCallV2(jsonRpcSession, poolAddress + "::"
                        + TOKEN_SWAP_SYRUP_MODULE_NAME + "::query_pool_info_v2",//
                Collections.singletonList(token),
                Collections.emptyList(),
                new TypeReference<List<BigInteger>>() {
                });
        return resultFields;
    }

    public static Triple<List<Long>, List<Long>, List<BigInteger>> syrupPoolQueryAllMultiplierPools(JSONRPC2Session jsonRpcSession,
                                                                                                    String poolAddress,
                                                                                                    String token) {
        List<Object> resultFields = contractCallV2(jsonRpcSession, poolAddress + "::"
                        + TOKEN_SWAP_SYRUP_MODULE_NAME + "::query_all_multiplier_pools",//
                Collections.singletonList(token),
                Collections.emptyList(),
                new TypeReference<List<Object>>() {
                });
        String keys = (String) resultFields.get(0);
        List<Long> multipliers = ((List<Object>) resultFields.get(1)).stream()
                .map(i -> Long.parseLong(i.toString())).collect(Collectors.toList());
        List<BigInteger> amounts = ((List<Object>) resultFields.get(2)).stream()
                .map(i -> new BigInteger(i.toString())).collect(Collectors.toList());
        byte[] keyBytes = HexUtils.hexToByteArray(keys);
        int iLen = keyBytes.length / multipliers.size();
        List<Long> secs = new ArrayList<>();
        for (int i = 0; i < multipliers.size(); i++) {
            byte[] item = Arrays.copyOfRange(keyBytes, i * iLen, i * iLen + Math.min(iLen, 8));
            BcsDeserializer deserializer = new BcsDeserializer(item);
            try {
                secs.add(deserializer.deserialize_u64());
            } catch (DeserializationError e) {
                throw new RuntimeException(e);
            }
        }
        return new Triple<>(secs, multipliers, amounts);
    }

    public static BigInteger getAccountVeStarAmount(JSONRPC2Session jsonRpcSession, String contractAddress, String accountAddress) {
        //TokenSwapSyrupScript::query_vestar_amount(user_addr: address)
        List<BigInteger> resultFields = contractCallV2(jsonRpcSession, contractAddress + "::"
                        + TOKEN_SWAP_SYRUP_SCRIPT_MODULE_NAME + "::query_vestar_amount",//
                Collections.emptyList(),
                Collections.singletonList(accountAddress),
                new TypeReference<List<BigInteger>>() {
                });
        if (resultFields.size() > 0) {
            return resultFields.get(0);
        } else {
            return null;
        }
    }

    public static BigInteger getAccountVeStarAmountByStakeId(JSONRPC2Session jsonRpcSession, String contractAddress, String accountAddress, Long stakeId, String tokenType) {
        //public fun query_vestar_amount_by_staked_id(user_addr: address, id: u64): u128 {
        List<BigInteger> resultFields = contractCallV2(jsonRpcSession, contractAddress + "::"
                        + TOKEN_SWAP_SYRUP_SCRIPT_MODULE_NAME + "::query_vestar_amount_by_staked_id",//
                Collections.emptyList(),
                Arrays.asList(accountAddress, stakeId.toString() + "u64"),
                new TypeReference<List<BigInteger>>() {
                });
        if (resultFields.size() > 0) {
            if (resultFields.get(0).compareTo(BigInteger.ZERO) > 0) {
                return resultFields.get(0);
            }
        }

        //public fun query_vestar_amount_by_staked_id_tokentype<TokenT: store>(user_addr: address, id: u64): u128 {
        resultFields = contractCallV2(jsonRpcSession, contractAddress + "::"
                        + TOKEN_SWAP_SYRUP_SCRIPT_MODULE_NAME + "::query_vestar_amount_by_staked_id_tokentype",//
                Collections.singletonList(tokenType),
                Arrays.asList(accountAddress, stakeId + "u64"),
                new TypeReference<List<BigInteger>>() {
                });
        if (resultFields.size() > 0) {
            return resultFields.get(0);
        } else {
            return null;
        }
    }

    // ------------------------


//    public static List<Object> tokenSwapOracleLibraryCurrentCumulativePrices(JSONRPC2Session jsonRpcSession, String lpTokenAddress, String tokenX, String tokenY) {
//        List<Object> resultFields = contractCallV2(jsonRpcSession, lpTokenAddress + "::"
//                        + TOKEN_SWAP_ORACLE_LIBRARY_MODULE_NAME + "::current_cumulative_prices",
//                Arrays.asList(tokenX, tokenY), Collections.emptyList(), new TypeReference<List<Object>>() {
//                });
//        return resultFields;
//    }
}
