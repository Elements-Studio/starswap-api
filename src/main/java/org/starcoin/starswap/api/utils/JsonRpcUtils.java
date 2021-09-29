package org.starcoin.starswap.api.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.starcoin.bean.Event;
import org.starcoin.jsonrpc.JSONRPC2Request;
import org.starcoin.jsonrpc.JSONRPC2Response;
import org.starcoin.jsonrpc.client.JSONRPC2Session;
import org.starcoin.jsonrpc.client.JSONRPC2SessionException;
import org.starcoin.starswap.api.data.model.Pair;
import org.starcoin.starswap.api.service.LiquidityPoolService;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;


public class JsonRpcUtils {

    private static final String SWAP_GET_AMOUNT_IN_OUT_FUNCTION_MODULE_NAME = "TokenSwapLibrary";
    private static final String SWAP_GET_RESERVES_FUNCTION_MODULE_NAME = "TokenSwapRouter";
    private static final String SWAP_FARM_QUERY_TOTAL_STAKE_FUNCTION_MODULE_NAME = "TokenSwapFarmScript";
    private static final String SWAP_FARM_QUERY_RELEASE_PER_SECOND_FUNCTION_MODULE_NAME = "TokenSwapFarmScript";

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
                        reserveOut.toString() + "u128"
                ), new TypeReference<List<String>>() {
                });
        return new BigInteger(resultFields.get(0));
    }

    public static BigInteger tokenSwapRouterGetAmountIn(JSONRPC2Session jsonRpcSession, String lpTokenAddress, BigInteger amountOut, BigInteger reserveIn, BigInteger reserveOut) {
        List<String> resultFields = contractCallV2(jsonRpcSession, lpTokenAddress + "::" + SWAP_GET_AMOUNT_IN_OUT_FUNCTION_MODULE_NAME + "::get_amount_in",
                Collections.emptyList(), Arrays.asList(
                        amountOut.toString() + "u128",
                        reserveIn.toString() + "u128",
                        reserveOut.toString() + "u128"
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

    /**
     * JSON RPC call method 'contract.call_v2'.
     *
     * @param functionId function Id.
     * @param typeArgs   type arguments.
     * @param args       arguments.
     * @return JSON RPC response body.
     */
    public static String contractCallV2(JSONRPC2Session jsonRpcSession, String functionId, List<String> typeArgs, List<Object> args) {
        String method = "contract.call_v2";
        List<Object> params = getContractCallV2Params(functionId, typeArgs, args);
        return callForObject(jsonRpcSession, method, params, String.class);
    }

    public static <T> T contractCallV2(JSONRPC2Session jsonRpcSession, String functionId, List<String> typeArgs, List<Object> args, TypeReference<T> typeReference) {
        String method = "contract.call_v2";
        List<Object> params = getContractCallV2Params(functionId, typeArgs, args);
        return callForObject(jsonRpcSession, method, params, typeReference);
    }

    private static List<Object> getContractCallV2Params(String functionId, List<String> typeArgs, List<Object> args) {
        Map<String, Object> singleParamMap = new HashMap<>();
        singleParamMap.put("function_id", functionId);
        singleParamMap.put("type_args", typeArgs);
        singleParamMap.put("args", args);
        List<Object> params = Collections.singletonList(singleParamMap);
        return params;
    }

    private static <T> T callForObject(JSONRPC2Session jsonRpcSession, String method, List<Object> params, TypeReference<T> typeRef) {
        return callForObject(jsonRpcSession, method, params, (result) -> {
            return getObjectMapper().convertValue(result, typeRef);
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> T callForObject(JSONRPC2Session jsonRpcSession, String method, List<Object> params, Class<T> objectClass) {
        return callForObject(jsonRpcSession, method, params, (result) -> {
            if (objectClass.isAssignableFrom(result.getClass())) {
                return (T) result;
            }
            if (objectClass.isAssignableFrom(String.class)) {
                return (T) result.toString();
            }
            return getObjectMapper().convertValue(result, objectClass);
        });
    }

    private static <T> T callForObject(JSONRPC2Session jsonRpcSession,
                                       String method, List<Object> params,
                                       Function<Object, T> resultConverter) {
        JSONRPC2Request request = new JSONRPC2Request(method, params, System.currentTimeMillis());
        JSONRPC2Response response;
        try {
            response = jsonRpcSession.send(request);
        } catch (JSONRPC2SessionException e) {
            LOG.error("JSON rpc error.", e);
            throw new RuntimeException(e);
        }
        if (response.indicatesSuccess()) {
            Object result = response.getResult();
            if (result != null) {
                return resultConverter.apply(result);
            } else {
                return null;
            }
        } else {
            String msg = "JSON RPC error. response error: '" + response.getError() + "'. Request: " + request;
            LOG.error(msg);
            throw new RuntimeException(msg);
        }
    }

    private static ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }


}
