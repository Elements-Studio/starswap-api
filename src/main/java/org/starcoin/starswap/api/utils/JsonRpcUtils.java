package org.starcoin.starswap.api.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.starcoin.bean.Event;
import org.starcoin.jsonrpc.client.JSONRPC2Session;
import org.starcoin.starswap.api.data.model.Pair;
import org.starcoin.starswap.api.data.model.SyrupStake;
import org.starcoin.starswap.api.service.LiquidityPoolService;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.starcoin.utils.JsonRpcClientUtils.callForObject;
import static org.starcoin.utils.JsonRpcClientUtils.contractCallV2;


public class JsonRpcUtils {

    private static final String SWAP_GET_AMOUNT_IN_OUT_FUNCTION_MODULE_NAME = "TokenSwapLibrary";
    private static final String SWAP_GET_RESERVES_FUNCTION_MODULE_NAME = "TokenSwapRouter";
    private static final String SWAP_FARM_QUERY_TOTAL_STAKE_FUNCTION_MODULE_NAME = "TokenSwapFarmScript";
    private static final String SWAP_FARM_QUERY_RELEASE_PER_SECOND_FUNCTION_MODULE_NAME = "TokenSwapFarmScript";

    private static final String SWAP_FEE_NUMERATOR = "3u64";
    private static final String SWAP_FEE_DENUMERATOR = "1000u64";

    private static final Logger LOG = LoggerFactory.getLogger(LiquidityPoolService.class);


    public static Event[] getEvents(JSONRPC2Session jsonRpcSession, Map<String, Object> eventFilter) {
        String method = "chain.get_events";
        Class<Event[]> objectClass = Event[].class;
        Event[] events = callForObject(jsonRpcSession, method, Collections.singletonList(eventFilter), objectClass);
        return events == null ? new Event[0] : events;
    }

    // public fun scaling_factor<TokenType: store>(): u128
    public static BigInteger tokenGetScalingFactor(JSONRPC2Session jsonRpcSession, String token) {
        List<BigInteger> resultFields = contractCallV2(jsonRpcSession, "0x1::Token::scaling_factor",
                Arrays.asList(token), Collections.emptyList(), new TypeReference<List<BigInteger>>() {
                });
        return resultFields.get(0);
    }

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
        List<String> resultFields = contractCallV2(jsonRpcSession, lpTokenAddress + "::TokenSwapRouter::total_liquidity",
                Arrays.asList(tokenX, tokenY), Collections.emptyList(), new TypeReference<List<String>>() {
                });
        return new BigInteger(resultFields.get(0));
    }

    // public fun get_reserves<X: store, Y: store>(): (u128, u128)
    public static Pair<BigInteger, BigInteger> tokenSwapRouterGetReserves(JSONRPC2Session jsonRpcSession, String lpTokenAddress, String tokenX, String tokenY) {
        List<String> resultFields = contractCallV2(jsonRpcSession, lpTokenAddress + "::" + SWAP_GET_RESERVES_FUNCTION_MODULE_NAME + "::get_reserves",
                Arrays.asList(tokenX, tokenY), Collections.emptyList(), new TypeReference<List<String>>() {
                });
        return new Pair<>(new BigInteger(resultFields.get(0)), new BigInteger(resultFields.get(1)));
    }

    // public fun get_amount_out(amount_in: u128, reserve_in: u128, reserve_out: u128): u128
    public static BigInteger tokenSwapRouterGetAmountOut(JSONRPC2Session jsonRpcSession, String lpTokenAddress, BigInteger amountIn, BigInteger reserveIn, BigInteger reserveOut) {
        List<String> resultFields = contractCallV2(jsonRpcSession, lpTokenAddress + "::" + SWAP_GET_AMOUNT_IN_OUT_FUNCTION_MODULE_NAME + "::get_amount_out",
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
        List<String> resultFields = contractCallV2(jsonRpcSession, lpTokenAddress + "::" + SWAP_GET_AMOUNT_IN_OUT_FUNCTION_MODULE_NAME + "::get_amount_in",
                Collections.emptyList(), Arrays.asList(
                        amountOut.toString() + "u128",
                        reserveIn.toString() + "u128",
                        reserveOut.toString() + "u128",
                        SWAP_FEE_NUMERATOR, SWAP_FEE_DENUMERATOR
                ), new TypeReference<List<String>>() {
                });
        return new BigInteger(resultFields.get(0));
    }

    //public fun query_total_stake<TokenX: store, TokenY: store>() : u128
    public static BigInteger tokenSwapFarmQueryTotalStake(JSONRPC2Session jsonRpcSession, String farmAddress, String tokenX, String tokenY) {
        List<String> resultFields = contractCallV2(jsonRpcSession, farmAddress + "::" + SWAP_FARM_QUERY_TOTAL_STAKE_FUNCTION_MODULE_NAME + "::query_total_stake",
                Arrays.asList(tokenX, tokenY), Collections.emptyList(), new TypeReference<List<String>>() {
                });
        return new BigInteger(resultFields.get(0));
    }

    public static BigInteger tokenSwapFarmQueryReleasePerSecond(JSONRPC2Session jsonRpcSession, String farmAddress, String tokenX, String tokenY) {
        List<String> resultFields = contractCallV2(jsonRpcSession, farmAddress + "::" + SWAP_FARM_QUERY_RELEASE_PER_SECOND_FUNCTION_MODULE_NAME + "::query_release_per_second",
                Arrays.asList(tokenX, tokenY), Collections.emptyList(), new TypeReference<List<String>>() {
                });
        return new BigInteger(resultFields.get(0));
    }

    public static Integer tokenSwapFarmGetRewardMultiplier(JSONRPC2Session jsonRpcSession, String farmAddress, String tokenX, String tokenY) {
        List<Object> resultFields = contractCallV2(jsonRpcSession, farmAddress + "::TokenSwapFarmScript::get_farm_multiplier",//
                Arrays.asList(tokenX, tokenY), Collections.emptyList(), new TypeReference<List<Object>>() {
                });
        return Integer.parseInt(resultFields.get(0).toString());
    }

    // ------------------------

    public static BigInteger syrupPoolQueryTotalStake(JSONRPC2Session jsonRpcSession, String poolAddress, String token) {
        List<String> resultFields = contractCallV2(jsonRpcSession, poolAddress + "::" + "TokenSwapSyrup" + "::query_total_stake",
                Collections.singletonList(token), Collections.emptyList(), new TypeReference<List<String>>() {
                });
        return new BigInteger(resultFields.get(0));
    }

    public static BigInteger syrupPoolQueryReleasePerSecond(JSONRPC2Session jsonRpcSession, String poolAddress, String token) {
        // Move code:
        // public fun query_info<TokenT: store>(): (u64, u128) acquires Syrup {
        // //...
        //   (syrup.multiplie, syrup.release_per_second)
        // }
        List<String> resultFields = contractCallV2(jsonRpcSession, poolAddress + "::" + "TokenSwapSyrup" + "::query_info", //query_release_per_second
                Collections.singletonList(token), Collections.emptyList(), new TypeReference<List<String>>() {
                });
        return new BigInteger(resultFields.get(1));
    }

//    public static Integer syrupPoolGetRewardMultiplier(JSONRPC2Session jsonRpcSession, String poolAddress, String token) {
//        List<Object> resultFields = contractCallV2(jsonRpcSession, poolAddress + "::" + "TokenSwapSyrup" + "::get_pool_multiplier",//
//                Collections.singletonList(token), Collections.emptyList(), new TypeReference<List<Object>>() {
//                });
//        return Integer.parseInt(resultFields.get(0).toString());
//    }

    public static List<Long> syrupPoolQueryStakeList(JSONRPC2Session jsonRpcSession, String token, String poolAddress, String accountAddress) {
        //todo RPC return List<List<Long>> ???
        List<Long> resultFields = contractCallV2(jsonRpcSession, poolAddress + "::" + "TokenSwapSyrup" + "::query_stake_list",//
                Collections.singletonList(token), Collections.singletonList(accountAddress), new TypeReference<List<Long>>() {
                });
        return resultFields;
    }

    public static SyrupStake syrupPoolGetStakeInfo(JSONRPC2Session jsonRpcSession, String token, String poolAddress, String accountAddress, Long id) {
        //public fun get_stake_info<TokenT: store>(user_addr: address, id: u64): (u64, u64, u64, u128) acquires SyrupStakeList {
        ////...
        //(
        //        stake.start_time,
        //        stake.end_time,
        //        stake.stepwise_multiplier,
        //        stake.token_amount
        //)
        List<Object> resultFields = contractCallV2(jsonRpcSession, poolAddress + "::" + "TokenSwapSyrup" + "::get_stake_info",//
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

    // ------------------------


    public static List<Object> tokenSwapOracleLibraryCurrentCumulativePrices(JSONRPC2Session jsonRpcSession, String lpTokenAddress, String tokenX, String tokenY) {
        List<Object> resultFields = contractCallV2(jsonRpcSession, lpTokenAddress + "::TokenSwapOracleLibrary::current_cumulative_prices",
                Arrays.asList(tokenX, tokenY), Collections.emptyList(), new TypeReference<List<Object>>() {
                });
        return resultFields;
    }
}
