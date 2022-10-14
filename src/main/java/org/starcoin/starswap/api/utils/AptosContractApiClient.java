package org.starcoin.starswap.api.utils;

import com.novi.serde.SerializationError;
import dev.aptos.bean.AccountResource;
import dev.aptos.bean.CoinInfo;
import dev.aptos.bean.Option;
import dev.aptos.bean.OptionalAggregator;
import dev.aptos.types.TypeInfo;
import dev.aptos.utils.NodeApiUtils;
import dev.aptos.utils.StructTagUtils;
import org.starcoin.starswap.api.data.model.Pair;
import org.starcoin.starswap.api.data.model.SyrupStake;
import org.starcoin.starswap.api.data.model.Triple;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class AptosContractApiClient implements ContractApiClient {

    private static final long DEFAULT_OPERATION_NUMERATOR = 10;
    private static final long DEFAULT_OPERATION_DENUMERATOR = 60;
    private static final long DEFAULT_POUNDAGE_NUMERATOR = 3;
    private static final long DEFAULT_POUNDAGE_DENUMERATOR = 1000;

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

    @Override
    public BigInteger tokenSwapFarmQueryTotalStake(String farmAddress, String tokenX, String tokenY) {
        //TokenSwapFarmScript::query_total_stake
        throw new UnsupportedOperationException();
    }

    @Override
    public BigInteger tokenSwapFarmQueryReleasePerSecond(String farmAddress, String tokenX, String tokenY) {
        //TokenSwapFarmScript::query_release_per_second
        throw new UnsupportedOperationException();
    }

    @Override
    public Pair<BigInteger, BigInteger> tokenSwapFarmQueryReleasePerSecondV2AndAssetTotalWeight(String farmAddress, String tokenX, String tokenY) {
        //TokenSwapFarmRouter::query_info_v2
        //TokenSwapFarmRouter::query_global_pool_info
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer tokenSwapFarmGetRewardMultiplier(String farmAddress, String tokenX, String tokenY) {
        //TokenSwapFarmScript::get_farm_multiplier
        throw new UnsupportedOperationException();
    }

    @Override
    public Long tokenSwapFarmGetBoostFactor(String farmAddress, String tokenX, String tokenY, String accountAddress) {
        //TokenSwapFarmRouter::get_boost_factor
        throw new UnsupportedOperationException();
    }

    @Override
    public BigInteger tokenSwapFarmGetAccountStakedLiquidity(String farmAddress, String tokenX, String tokenY, String accountAddress) {
        //TokenSwapFarmScript::query_stake
        return null;//todo
    }

    @Override
    public BigInteger syrupPoolQueryTotalStake(String poolAddress, String token) {
        //List<BigInteger> info = syrupPoolQueryPoolInfoV2(jsonRpcSession, poolAddress, token);
        //return info.get(1);
        throw new UnsupportedOperationException();
    }

    @Override
    public BigInteger syrupPoolQueryReleasePerSecondV2(String poolAddress, String token) {
//        List<BigInteger> syrupTotalInfo = syrupPoolQuerySyrupInfo(jsonRpcSession, poolAddress);
//        BigInteger total_alloc_point = syrupTotalInfo.get(0);
//        BigInteger pool_release_per_second = syrupTotalInfo.get(1);
//        List<BigInteger> poolInfo = syrupPoolQueryPoolInfoV2(jsonRpcSession, poolAddress, token);
//        BigInteger alloc_point = poolInfo.get(0);
//        return pool_release_per_second.multiply(alloc_point).divide(total_alloc_point);
        throw new UnsupportedOperationException();
    }

    @Override
    public List<SyrupStake> syrupPoolQueryStakeList(String poolAddress, String token, String accountAddress) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<SyrupStake> syrupPoolQueryStakeWithExpectedGainList(String poolAddress, String token, String accountAddress) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Triple<List<Long>, List<Long>, List<BigInteger>> syrupPoolQueryAllMultiplierPools(String poolAddress, String token) {
        //TokenSwapSyrup::query_all_multiplier_pools
        throw new UnsupportedOperationException();
    }

    @Override
    public BigInteger getAccountVestarAmount(String accountAddress) {
        //query_vestar_amount
        //todo
        return null;
    }

    @Override
    public BigInteger getAccountVestarAmountByStakeId(String accountAddress, Long stakeId, String tokenTypeTag) {
        //query_vestar_amount_by_staked_id
        throw new UnsupportedOperationException();
    }

    @Override
    public BigInteger tokenSwapRouterGetTotalLiquidity(String lpTokenAddress, String tokenX, String tokenY) {
        //TokenSwapRouter::total_liquidity
        throw new UnsupportedOperationException();
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

    @Override
    public Pair<BigInteger, BigInteger> tokenSwapRouterGetReserves(String lpTokenAddress, String tokenX, String tokenY) {
        Pair<String, String> tp = sortTokens(tokenX, tokenY);
        String resourceType = lpTokenAddress + "::TokenSwap::TokenSwapPair<" + tp.getItem1() + ", " + tp.getItem2() + ">";
        try {
            AccountResource<Map> resource = NodeApiUtils.getAccountResource(nodeApiBaseUrl, lpTokenAddress, resourceType, Map.class, null);
            Map r1 = (Map) resource.getData().get("token_x_reserve");
            Map r2 = (Map) resource.getData().get("token_y_reserve");
            BigInteger v1 = new BigInteger(r1.get("value").toString());
            BigInteger v2 = new BigInteger(r2.get("value").toString());
            return tokenX.equals(tp.getItem1()) ? new Pair<>(v1, v2) : new Pair<>(v2, v1);
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

    /// Boost multiplier calculation follows the formula
    /// @param The amount of Vestar staked by users
    /// @param The user's pledge amount on the current farm
    /// @param Total stake on the current farm
    /// return Boost factor Max:250
    /// `boost factor = ( UserLockedVeSTARAmount / TotalVeSTARAmount ) / ( ( 2 / 3) * UserLockedFarmAmount / TotalLockedFarmAmount ) + 1  `
    public BigInteger computeBoostFactor(BigInteger user_locked_vestar_amount,
                                         BigInteger user_locked_farm_amount,
                                         BigInteger total_farm_amount) {
//    public fun compute_boost_factor(user_locked_vestar_amount: u128,
//                                    user_locked_farm_amount: u128,
//                                    total_farm_amount: u128): u64 {
//        let factor = (math64::pow(10, 8) as u128);
//
//        let total_vestar_amount = option::get_with_default(&coin::supply<VESTAR::VESTAR>(), 0u128);
//
//        let boost_factor = 1 * factor;
//        let dividend = SafeMath::mul_div(user_locked_vestar_amount, factor * 3, total_vestar_amount) * factor;
//        let divisor  = SafeMath::mul_div(user_locked_farm_amount, factor * 2, total_farm_amount);
//
//        if(divisor != 0){
//            boost_factor = ( dividend / divisor ) + boost_factor;
//        };
//        if (boost_factor > (25 * factor / 10)) {
//            boost_factor = 25 * factor / 10;
//        }else if( ( 1 * factor ) < boost_factor && boost_factor <  ( 1 * factor + 1 * ( factor / 100 ) ) ){
//            boost_factor =  1 * factor + 1 * ( factor / 100 ) ;
//        };
//        let boost_factor = boost_factor / ( factor / 100 );
//        return (boost_factor as u64)
        return null;//todo
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
        return null;
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

    public BigInteger getVestarAmountByTokenTypeAndStakeId(String accountAddress, String token, Long stakeId) {
        //query_vestar_amount_by_staked_id_tokentype
        //todo
        return null;
    }

}
