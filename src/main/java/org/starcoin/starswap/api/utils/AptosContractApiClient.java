package org.starcoin.starswap.api.utils;

import com.novi.serde.SerializationError;
import dev.aptos.bean.AccountResource;
import dev.aptos.types.TypeInfo;
import dev.aptos.utils.NodeApiUtils;
import dev.aptos.utils.StructTagUtils;
import org.starcoin.starswap.api.data.model.Pair;
import org.starcoin.starswap.api.data.model.SyrupStake;
import org.starcoin.starswap.api.data.model.Triple;
import org.starcoin.starswap.api.vo.AccountFarmStakeInfo;

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
        throw new UnsupportedOperationException();
    }

    @Override
    public BigInteger tokenSwapFarmQueryReleasePerSecond(String farmAddress, String tokenX, String tokenY) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pair<BigInteger, BigInteger> tokenSwapFarmQueryReleasePerSecondV2AndAssetTotalWeight(String farmAddress, String tokenX, String tokenY) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer tokenSwapFarmGetRewardMultiplier(String farmAddress, String tokenX, String tokenY) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long tokenSwapFarmGetBoostFactor(String farmAddress, String tokenX, String tokenY, String accountAddress) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigInteger syrupPoolQueryTotalStake(String poolAddress, String token) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigInteger syrupPoolQueryReleasePerSecondV2(String poolAddress, String token) {
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
        throw new UnsupportedOperationException();
    }

    @Override
    public BigInteger getAccountVeStarAmount(String accountAddress) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigInteger getAccountVeStarAmountByStakeId(String accountAddress, Long stakeId, String tokenTypeTag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AccountFarmStakeInfo getAccountFarmStakeInfo(String farmAddress, String lpTokenAddress, String tokenX, String tokenY, String accountAddress) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pair<BigInteger, BigInteger> getTokenSwapFarmStakedReserves(String farmAddress, String lpTokenAddress, String tokenX, String tokenY) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pair<BigInteger, BigInteger> getReservesByLiquidity(String lpTokenAddress, String tokenX, String tokenY, BigInteger liquidity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigInteger tokenSwapRouterGetTotalLiquidity(String lpTokenAddress, String tokenX, String tokenY) {
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
}
