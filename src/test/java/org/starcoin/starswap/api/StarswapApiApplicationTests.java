package org.starcoin.starswap.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.starcoin.starswap.api.data.model.*;
import org.starcoin.starswap.api.data.repo.*;
import org.starcoin.starswap.api.service.*;
import org.starcoin.starswap.api.utils.JsonRpcClient;
import org.starcoin.starswap.api.vo.AccountFarmStakeInfo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.List;
import java.util.UUID;

@SpringBootTest
class StarswapApiApplicationTests {

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    TokenService tokenService;

    @Autowired
    LiquidityTokenRepository liquidityTokenRepository;

    @Autowired
    LiquidityPoolRepository liquidityPoolRepository;

    @Autowired
    LiquidityAccountRepository liquidityAccountRepository;

    @Autowired
    LiquidityTokenService liquidityTokenService;

    @Autowired
    LiquidityPoolService liquidityPoolService;

    @Autowired
    LiquidityTokenFarmRepository liquidityTokenFarmRepository;

    @Autowired
    LiquidityTokenFarmAccountRepository liquidityTokenFarmAccountRepository;

    @Autowired
    NodeHeartbeatRepository nodeHeartbeatRepository;

    @Autowired
    NodeHeartbeatService nodeHeartbeatService;

    @Autowired
    OnChainService onChainService;

    @Autowired
    PullingEventTaskService pullingEventTaskService;

    @Autowired
    TokenPriceService tokenPriceService;

    @Autowired
    LiquidityTokenFarmService liquidityTokenFarmService;

    public static void addNodeHeartbeat(NodeHeartbeatRepository nodeHeartbeatRepository, NodeHeartbeat b7) {
        b7.setCreatedAt(System.currentTimeMillis());
        b7.setCreatedBy("admin");
        b7.setUpdatedAt(b7.getCreatedAt());
        b7.setUpdatedBy(b7.getCreatedBy());
        nodeHeartbeatRepository.save(b7);
    }

    @Test
    void contextLoads() {

        try {
            JsonRpcClient jsonRpcClient = new JsonRpcClient("https://main-seed.starcoin.org");
            AccountFarmStakeInfo accountFarmStakeInfo = jsonRpcClient.getAccountFarmStakeInfo(
                    "0x8c109349c6bd91411d6bc962e080c4a3",
                    "0x8c109349c6bd91411d6bc962e080c4a3",
                    "0x8c109349c6bd91411d6bc962e080c4a3::STAR::STAR",
                    "0x00000000000000000000000000000001::STC::STC",
                    "...");
            System.out.println(accountFarmStakeInfo);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (true) return;

        LiquidityTokenFarm farm = liquidityTokenFarmService.findOneByTokenIdPair("FAI", "STC");
        Integer farmMultiplier = onChainService.getFarmRewardMultiplier(farm);
        System.out.println(farmMultiplier);

        if (true) return;
        BigDecimal exchangeRate1 = onChainService.getExchangeRate(
                "0x00000000000000000000000000000001::STC::STC",
                "0xfe125d419811297dfab03c61efec0bc9::FAI::FAI");
        System.out.println("STC / FAI: " + exchangeRate1);
        BigDecimal exchangeRate2 = onChainService.getExchangeRate(
                "0x4783d08fb16990bd35d83f3e23bf93b8::STAR::STAR",
                "0x00000000000000000000000000000001::STC::STC");
        System.out.println("STAR / STC: " + exchangeRate2);

        BigDecimal exchangeRate3 = onChainService.getToUsdExchangeRate("0x4783d08fb16990bd35d83f3e23bf93b8::STAR::STAR");
        System.out.println("STAR / USD: " + exchangeRate3);
        if (true) return;

        BigDecimal exchangeRateStcToUsd2 = onChainService.getToUsdExchangeRate("0x00000000000000000000000000000001::STC::STC");
        //BigDecimal exchangeRateStcToUsd3 = onChainService.getToUsdExchangeRate("0x00000000000000000000000000000001::STC::STC");
        System.out.println("STC / USD: " + exchangeRateStcToUsd2);
        if (true) return;
        BigDecimal stcUsd = tokenPriceService.getToUsdExchangeRateByTokenId("STC");
        BigDecimal stcUsd2 = tokenPriceService.getToUsdExchangeRateByTokenId("STC");
        System.out.println(stcUsd);
        System.out.println(stcUsd2);
        BigDecimal ethUsd = tokenPriceService.getToUsdExchangeRateByTokenId("ETH");
        System.out.println(ethUsd);
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BigDecimal ethUsd2 = tokenPriceService.getToUsdExchangeRateByTokenId("ETH");
        System.out.println(ethUsd2);
        if (true) return;

        String bxUsdtToUsdPId = tokenPriceService.getToUsdPricePairIdByTokenId("BX_USDT");
        System.out.println(bxUsdtToUsdPId);
        String stcToUsdPId = tokenPriceService.getToUsdPricePairIdByTokenId("STC");
        System.out.println(stcToUsdPId);
        String btcToUsdPId = tokenPriceService.getToUsdPricePairIdByTokenId("BTC");
        System.out.println(btcToUsdPId);
        if (true) return;
        String tokenAddress = "0x598b8cbfd4536ecbe88aa1cfaffa7a62";
        String tokenModule = "TBD";
        String tokenName = "TBD";
        Token token = tokenService.getTokenByStructType(tokenAddress, tokenModule, tokenName);
        Token token1 = tokenService.getTokenByStructType(tokenAddress, tokenModule, tokenName);
        Token token2 = tokenService.getTokenByStructType(new StructType(tokenAddress, tokenModule, tokenName));
        if (true) return;

        Pair<List<String>, BigInteger> swapPathStcUsdt = onChainService.getBestSwapPathAndAmountOut("STC", "BX_USDT", BigInteger.valueOf(100000L));
        System.out.println(swapPathStcUsdt);
        if (true) return;
        Pair<List<String>, BigInteger> swapPathBotInDddOut = onChainService.getBestSwapPathAndAmountOut("Bot", "Ddd", BigInteger.valueOf(100000L));
        System.out.println(swapPathBotInDddOut); // Pair{item1=[Bot, Usdx, Ddd], item2=993992}
        Pair<List<String>, BigInteger> swapPathDddOutBotIn = onChainService.getBestSwapPathAndAmountIn("Bot", "Ddd", BigInteger.valueOf(9939L));
        System.out.println(swapPathDddOutBotIn);
        if (true) return;
//        List<String> swapPathBotDdd = liquidityTokenService.getShortestIndirectSwapPath("Bot", "Ddd");
//        System.out.println(swapPathBotDdd);
        List<String> swapPathTbdDdd = liquidityTokenService.getShortestIndirectSwapPath("TBD", "Ddd");
        System.out.println(swapPathTbdDdd);
        List<String> swapPathBotTbd = liquidityTokenService.getShortestIndirectSwapPath("Bot", "TBD");
        System.out.println(swapPathBotTbd);
        if (true) return;
//        nodeHeartbeatService.beat(BigInteger.valueOf(1));
//        nodeHeartbeatService.beat(BigInteger.TEN);
//        nodeHeartbeatService.beat(BigInteger.valueOf(12));
//        nodeHeartbeatService.reset();
//        nodeHeartbeatService.beat(BigInteger.valueOf(15));
//        nodeHeartbeatService.beat(BigInteger.valueOf(17));
//        if (true) return;

        BigDecimal estimatedApy = onChainService.getFarmEstimatedApyByTokenIdPair("Bot", "Ddd");
        System.out.println(estimatedApy);
        if (true) return;

        Pair<BigInteger, BigInteger> stakedReserves = onChainService.getFarmStakedReservesByTokenIdPair("Bot", "Ddd");
        System.out.println(stakedReserves);
        Pair<BigInteger, BigInteger> stakedReserves2 = onChainService.getFarmStakedReservesByTokenIdPair("Bot", "Usdx");
        System.out.println(stakedReserves2);
        //        tryRun(() -> addTestLiquidityToken("Bot", "Ddd"));
        //        tryRun(() -> addTestLiquidityToken("Bot", "Usdx"));
        //        tryRun(() -> addTestLiquidityToken("Ddd", "Usdx"));
        //        tryRun(() -> addTestLiquidityToken("TBD", "Usdx"));
        //        if (true) return;
        // need token swap pair in database...
        BigDecimal exchangeRateTbdToUsd = onChainService.getToUsdExchangeRate("0x598b8cbfd4536ecbe88aa1cfaffa7a62::TBD::TBD");
        System.out.println("TBD / USD: " + exchangeRateTbdToUsd);
        BigDecimal exchangeRateDddToUsd = onChainService.getToUsdExchangeRate("0x598b8cbfd4536ecbe88aa1cfaffa7a62::Ddd::Ddd");
        System.out.println("Ddd / USD: " + exchangeRateDddToUsd);
        BigDecimal exchangeRateBotToUsd = onChainService.getToUsdExchangeRate("0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot");
        System.out.println("Bot / USD: " + exchangeRateBotToUsd);
        if (true) return;

        //pullingEventTaskService.updatePullingEventTask(BigInteger.valueOf(42), BigInteger.valueOf(43));
        //if (true) return;

        addTestNodeHeartbeats();
        //if (true) return;
        tryRun(() -> addTestToken("TBD", 90));

        tryRun(() -> addTestToken("Bot", 90));

        tryRun(() -> addTestToken("Ddd", 99));

        tryRun(() -> addTestToken("Usdx", 99));

        tryRun(() -> addTestLiquidityToken("Bot", "Ddd"));
        tryRun(() -> addTestLiquidityToken("Bot", "Usdx"));
        tryRun(() -> addTestLiquidityToken("Ddd", "Usdx"));
        tryRun(() -> addTestLiquidityToken("TBD", "Usdx"));

        tryRun(this::addTestLiquidityPool);

        tryRun(this::addTestLiquidityAccount);

        tryRun(this::addTestLiquidityTokenFarm);

        tryRun(this::addTestFarmAccount);

        // test queries...
        System.out.println(liquidityTokenService.findOneByTokenIdPair("Bot", "Ddd"));
        System.out.println(liquidityPoolService.findOneByTokenIdPair("Bot", "Ddd"));
    }

    private void addTestNodeHeartbeats() {
        NodeHeartbeat b7 = new NodeHeartbeat();
        b7.setNodeId("0x" + UUID.randomUUID().toString().replace("-", ""));
        b7.setStartedAt(BigInteger.valueOf(79));
        b7.setBeatenAt(BigInteger.valueOf(92));
        addNodeHeartbeat(nodeHeartbeatRepository, b7);

        NodeHeartbeat b6 = new NodeHeartbeat();
        b6.setNodeId("0x" + UUID.randomUUID().toString().replace("-", ""));
        b6.setStartedAt(BigInteger.valueOf(91));
        b6.setBeatenAt(BigInteger.valueOf(100));
        addNodeHeartbeat(nodeHeartbeatRepository, b6);

        NodeHeartbeat b5 = new NodeHeartbeat();
        b5.setNodeId("0x" + UUID.randomUUID().toString().replace("-", ""));
        b5.setStartedAt(BigInteger.valueOf(60));
        b5.setBeatenAt(BigInteger.valueOf(71));
        addNodeHeartbeat(nodeHeartbeatRepository, b5);

        NodeHeartbeat b4 = new NodeHeartbeat();
        b4.setNodeId("0x" + UUID.randomUUID().toString().replace("-", ""));
        b4.setStartedAt(BigInteger.valueOf(71));
        b4.setBeatenAt(BigInteger.valueOf(80));
        addNodeHeartbeat(nodeHeartbeatRepository, b4);

        NodeHeartbeat b3 = new NodeHeartbeat();
        b3.setNodeId("0x" + UUID.randomUUID().toString().replace("-", ""));
        b3.setStartedAt(BigInteger.valueOf(51));
        b3.setBeatenAt(BigInteger.valueOf(60));
        addNodeHeartbeat(nodeHeartbeatRepository, b3);

        NodeHeartbeat b2 = new NodeHeartbeat();
        b2.setNodeId("0x" + UUID.randomUUID().toString().replace("-", ""));
        b2.setStartedAt(BigInteger.valueOf(21));
        b2.setBeatenAt(BigInteger.valueOf(30));
        addNodeHeartbeat(nodeHeartbeatRepository, b2);

        NodeHeartbeat b1 = new NodeHeartbeat();
        b1.setNodeId("0x" + UUID.randomUUID().toString().replace("-", ""));
        b1.setStartedAt(BigInteger.valueOf(1));
        b1.setBeatenAt(BigInteger.valueOf(10));
        addNodeHeartbeat(nodeHeartbeatRepository, b1);

        List<Object[]> breakpoints = nodeHeartbeatRepository.findBreakpoints();
        breakpoints.forEach(p -> System.out.println(p[0] + "\t" + p[1]));

        System.out.println(nodeHeartbeatService.findBreakIntervals());
    }

    private void tryRun(Runnable runnable) {
        try {
            runnable.run();
        } catch (RuntimeException runtimeException) {
            runtimeException.printStackTrace();
        }
    }

    private void addTestFarmAccount() {
        LiquidityTokenFarmAccount farmAccount = new LiquidityTokenFarmAccount();//  values (
        farmAccount.setFarmAccountId(new LiquidityTokenFarmAccountId(
                "0x598b8cbfd4536ecbe88aa1cfaffa7a62", new LiquidityTokenFarmId(
                new LiquidityTokenId("Bot", "Ddd", "0x598b8cbfd4536ecbe88aa1cfaffa7a62"),
                "0x598b8cbfd4536ecbe88aa1cfaffa7a62")));//  '0x598b8cbfd4536ecbe88aa1cfaffa7a62',
        //  '0x598b8cbfd4536ecbe88aa1cfaffa7a62',
        //  'Bot',
        //  'Ddd',
        farmAccount.setCreatedAt(System.currentTimeMillis());//  unix_timestamp(now()),
        farmAccount.setCreatedBy("admin");//  'admin',
        farmAccount.setDeactived(false);//  false,
        farmAccount.setStakeAmount(BigInteger.valueOf(1000000L));//  10000000,
        farmAccount.setUpdatedAt(System.currentTimeMillis());//  unix_timestamp(now()),
        farmAccount.setUpdatedBy("admin");//  'admin'
        liquidityTokenFarmAccountRepository.save(farmAccount);
        //  )
        //  ;
    }


    private void addTestLiquidityTokenFarm() {
        //
        // insert into pulling_event_task
        //	(from_block_number,
        //    created_at,
        //    created_by,
        //    status,
        //    to_block_number,
        //    updated_at,
        //    updated_by
        //    ) values (
        //    0,
        //    unix_timestamp(now()),
        //    'ADMIN',
        //    'CREATED',
        //    200,
        //    unix_timestamp(now()),
        //    'ADMIN'
        //    );

        LiquidityTokenFarm liquidityTokenFarm = new LiquidityTokenFarm();
        liquidityTokenFarm.setLiquidityTokenFarmId(new LiquidityTokenFarmId(
                new LiquidityTokenId("Bot", "Ddd", "0x598b8cbfd4536ecbe88aa1cfaffa7a62"),
                "0x598b8cbfd4536ecbe88aa1cfaffa7a62"));
        liquidityTokenFarm.setCreatedAt(System.currentTimeMillis());//  unix_timestamp(now()),
        liquidityTokenFarm.setCreatedBy("admin");//  'admin',
        liquidityTokenFarm.setDeactived(false);//  false,
        liquidityTokenFarm.setDescription("Bot<->Ddd Pool");
        //  'Bot<->Ddd Pool',
        liquidityTokenFarm.setSequenceNumber(11);//  11,
        liquidityTokenFarm.setUpdatedAt(System.currentTimeMillis());//  unix_timestamp(now()),
        liquidityTokenFarm.setUpdatedBy("admin");//  'admin'
        liquidityTokenFarm.setRewardTokenId("TBD");
        liquidityTokenFarmRepository.save(liquidityTokenFarm);

        List<LiquidityTokenFarm> liquidityTokenFarms = liquidityTokenFarmRepository.findByLiquidityTokenFarmIdTokenXIdAndLiquidityTokenFarmIdTokenYId("Bot", "Ddd");
        System.out.println("Get liquidity token farms: " + liquidityTokenFarms.size());
    }

    private void addTestLiquidityAccount() {
        //  -- ?????????????????????????????????????????????
        //  insert into `liquidity_account` (
        //  `account_address`,
        //  `pool_address`,
        //  `token_x_id`,
        //  `token_y_id`,
        //  `created_at`,
        //  `created_by`,
        //  `deactived`,
        //  `liquidity`,
        //  `updated_at`,
        //  `updated_by`
        //  )
        LiquidityAccount liquidityAccount = new LiquidityAccount();//  values (
        liquidityAccount.setLiquidityAccountId(new LiquidityAccountId(
                "0x598b8cbfd4536ecbe88aa1cfaffa7a62", new LiquidityPoolId(
                new LiquidityTokenId("Bot", "Ddd", "0x598b8cbfd4536ecbe88aa1cfaffa7a62"),
                "0x598b8cbfd4536ecbe88aa1cfaffa7a62")));//  '0x598b8cbfd4536ecbe88aa1cfaffa7a62',
        //  '0x598b8cbfd4536ecbe88aa1cfaffa7a62',
        //  'Bot',
        //  'Ddd',
        liquidityAccount.setCreatedAt(System.currentTimeMillis());//  unix_timestamp(now()),
        liquidityAccount.setCreatedBy("admin");//  'admin',
        liquidityAccount.setDeactived(false);//  false,
        liquidityAccount.setLiquidity(BigInteger.valueOf(1000000L));//  10000000,
        liquidityAccount.setUpdatedAt(System.currentTimeMillis());//  unix_timestamp(now()),
        liquidityAccount.setUpdatedBy("admin");//  'admin'
        liquidityAccountRepository.save(liquidityAccount);
        //  )
        //  ;

    }

    private void addTestLiquidityPool() {
        //-- ????????????
        //insert into `token_pair_pool` (
        //  `pool_address`,
        //  `token_x_id`,
        //  `token_y_id`,
        //  `created_at`,
        //  `created_by`,
        //  `deactived`,
        //  `description`,
        //  `description_en`,
        //  `sequence_number`,
        //  `updated_at`,
        //  `updated_by`)
        //  values (
        LiquidityPool liquidityPool = new LiquidityPool();
        liquidityPool.setLiquidityPoolId(new LiquidityPoolId(
                new LiquidityTokenId("Bot", "Ddd", "0x598b8cbfd4536ecbe88aa1cfaffa7a62"),
                "0x598b8cbfd4536ecbe88aa1cfaffa7a62"));
        liquidityPool.setCreatedAt(System.currentTimeMillis());//  unix_timestamp(now()),
        liquidityPool.setCreatedBy("admin");//  'admin',
        liquidityPool.setDeactived(false);//  false,
        liquidityPool.setDescription("Bot<->Ddd Pool");
        //  'Bot<->Ddd Pool',
        liquidityPool.setSequenceNumber(11);//  11,
        liquidityPool.setUpdatedAt(System.currentTimeMillis());//  unix_timestamp(now()),
        liquidityPool.setUpdatedBy("admin");//  'admin'
        liquidityPoolRepository.save(liquidityPool);

        List<LiquidityPool> liquidityPools = liquidityPoolRepository.findByLiquidityPoolIdTokenXIdAndLiquidityPoolIdTokenYId("Bot", "Ddd");
        System.out.println("Get liquidity pools: " + liquidityPools.size());

        //  )
        //  ;
        //
    }

    private void addTestLiquidityToken(String tokenXId, String tokenYId) {
        //-- token pair
        //insert into token_pair (  `token_x_id`, `token_y_id`,
        //  `created_at`,
        //  `created_by`,
        //  `deactived`,
        //  `description`,
        //  `description_en`,
        //  `sequence_number`,
        //  `default_pool_address`, -- ????????????????????????
        //  `token_x_struct_address`,
        //  `token_x_struct_module`,
        //  `token_x_struct_name`,
        //  `token_y_struct_address`,
        //  `token_y_struct_module`,
        //  `token_y_struct_name`,
        //  `updated_at`,
        //  `updated_by`)
        LiquidityToken botDdd = new LiquidityToken();
        botDdd.setLiquidityTokenId(new LiquidityTokenId(tokenXId, tokenYId, "0x598b8cbfd4536ecbe88aa1cfaffa7a62"));//  values ( 'Bot', 'Ddd',
        botDdd.setCreatedAt(System.currentTimeMillis());//  UNIX_TIMESTAMP(now()),
        botDdd.setCreatedBy("admin");//  'admin',
        botDdd.setDeactived(false);//  false,
        botDdd.setDescription(tokenXId + "<->" + tokenYId);
        botDdd.setSequenceNumber(99);
        botDdd.setTokenXStructType(new StructType("0x598b8cbfd4536ecbe88aa1cfaffa7a62", "Bot", "Bot"));
        botDdd.setTokenYStructType(new StructType("0x598b8cbfd4536ecbe88aa1cfaffa7a62", "Ddd", "Ddd"));
        //  '0x598b8cbfd4536ecbe88aa1cfaffa7a62',
        //  'Bot',
        //  'Bot',
        //  '0x598b8cbfd4536ecbe88aa1cfaffa7a62',
        //  'Ddd',
        //  'Ddd',
        botDdd.setUpdatedAt(System.currentTimeMillis());//  UNIX_TIMESTAMP(now()),
        botDdd.setUpdatedBy("admin");//  'admin')
        liquidityTokenRepository.save(botDdd);
    }

    private void addTestToken(String tokenId, int seqNumber) {
        //-- insert test tokens and token pair...
        //insert into token (
        //  `token_id`,
        //  `created_at`,
        //  `created_by`,
        //  `deactived`,
        //  `description`,
        //  `description_en`,
        //  `icon_url`,
        //  `sequence_number`,
        //  `token_struct_address`,
        //  `token_struct_module`,
        //  `token_struct_name`,
        //  `updated_at`,
        //  `updated_by`)
        Token ddd = new Token();
        ddd.setTokenId(tokenId);//  values ( 'Ddd',
        ddd.setCreatedAt(System.currentTimeMillis());//  UNIX_TIMESTAMP(now()),
        ddd.setCreatedBy("admin");//  'admin',
        ddd.setDeactived(false);//  false,
        ddd.setDescription(tokenId);//  'Ddd',
        ddd.setIconUrl("http://starcoin.org/unknown-token-icon.jpg");
        ddd.setSequenceNumber(seqNumber);//  99,
        ddd.setTokenStructType(new StructType("0x598b8cbfd4536ecbe88aa1cfaffa7a62",
                tokenId,
                tokenId));
        ddd.setUpdatedAt(System.currentTimeMillis());//  UNIX_TIMESTAMP(now()),
        ddd.setUpdatedBy("admin");//  'admin')
        //ddd.setVersion(2L);
        tokenRepository.save(ddd);
        //if (true) return;
    }

}
