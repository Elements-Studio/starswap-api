package org.starcoin.starswap.api;

import java.io.File;
import java.io.IOException;

public class DevNetworkInteractApp {

    public static void main(String[] args) {
        // ------------------------------------
        // 先编译 move 代码：
        // move clean && move publish
        // ------------------------------------
        String shellPath = "/bin/sh";
        String starcoinCmd = "/Users/yangjiefeng/Documents/starcoinorg/starcoin/target/debug/starcoin -n dev -d alice console";
        String moveProjectDir = "/Users/yangjiefeng/Documents/starcoinorg/starswap-core";
        if (args.length < 2) {
            throw new IllegalArgumentException("Please enter two account private keys");
        }
        String firstPrivateKey = args[0];
        String secondPrivateKey = args[1];
        Process process = null;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    //new String[] {starcoinCmd, "-n", "dev", "console"}
                    shellPath, "-c", starcoinCmd);
            processBuilder.directory(new File(moveProjectDir));
            //processBuilder.inheritIO();
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        if (process == null) {
            throw new NullPointerException();
        }
        boolean init = false;
        CommandLineInteractor commandLineInteractor = new CommandLineInteractor(process);
        commandLineInteractor.expect("Start console,", 30);
        if (init) {
            // 导入账户，部署合约
            commandLineInteractor.sendLine("account import -i " + firstPrivateKey)
                    .expect("\"ok\":", 10)
                    .sendLine("account import -i " + secondPrivateKey)
                    .expect("\"ok\":", 10)
                    .sendLine("account default 0x598b8cbfd4536ecbe88aa1cfaffa7a62")
                    .expect("\"ok\":", 10)
                    .sendLine("account unlock 0x598b8cbfd4536ecbe88aa1cfaffa7a62")
                    .expect("\"ok\":", 10)
                    .sendLine("account unlock 0xff2794187d72cc3a9240198ca98ac7b6")
                    .expect("\"ok\":", 10)
                    .sendLine("dev get-coin 0x598b8cbfd4536ecbe88aa1cfaffa7a62")
                    .expect("\"ok\":", 10)
                    .sendLine("dev get-coin 0xff2794187d72cc3a9240198ca98ac7b6")
                    .expect("\"ok\":", 10)
            ;

//                    .sendLine("dev deploy storage/0x598b8cbfd4536ecbe88aa1cfaffa7a62/modules/TokenSwap.mv -b")
//                    .expect("\"ok\":", 10)
//                    .sendLine("dev deploy storage/0x598b8cbfd4536ecbe88aa1cfaffa7a62/modules/TokenSwapRouter.mv -b")
//                    .expect("\"ok\":", 10)
//                    .sendLine("dev deploy storage/0x598b8cbfd4536ecbe88aa1cfaffa7a62/modules/TokenSwapScripts.mv -b")
//                    .expect("\"ok\":", 10)
//                    .sendLine("dev deploy storage/0x598b8cbfd4536ecbe88aa1cfaffa7a62/modules/TBD.mv -b")
//                    .expect("\"ok\":", 10)
//                    .sendLine("dev deploy storage/0x598b8cbfd4536ecbe88aa1cfaffa7a62/modules/TokenSwapFarm.mv -b")
//                    .expect("\"ok\":", 10)
//                    .sendLine("dev deploy storage/0x598b8cbfd4536ecbe88aa1cfaffa7a62/modules/TokenSwapGovPoolType.mv -b")
//                    .expect("\"ok\":", 10)
//                    .sendLine("dev deploy storage/0x598b8cbfd4536ecbe88aa1cfaffa7a62/modules/TokenSwapGov.mv -b")
//                    .expect("\"ok\":", 10)
//                    .sendLine("dev deploy storage/0x598b8cbfd4536ecbe88aa1cfaffa7a62/modules/TokenSwapFarmScript.mv -b") //todo named 'XxxxScripts'???
//                    .expect("\"ok\":", 10)
//                    .sendLine("dev deploy storage/0x598b8cbfd4536ecbe88aa1cfaffa7a62/modules/TokenSwapGovScript.mv -b")
//                    .expect("\"ok\":", 10)
            for (String c : deployMoveCodeCommands()) {
                commandLineInteractor.sendLine(c)
                        .expect("\"ok\":", 10);
            }

            // //////////////////
            commandLineInteractor.sendLine("dev deploy storage/0x598b8cbfd4536ecbe88aa1cfaffa7a62/modules/Bot.mv -b")
                    .expect("\"ok\":", 10)
                    .sendLine("dev deploy storage/0x598b8cbfd4536ecbe88aa1cfaffa7a62/modules/Ddd.mv -b")
                    .expect("\"ok\":", 10)

                    .sendLine("dev deploy storage/0x598b8cbfd4536ecbe88aa1cfaffa7a62/modules/Usdx.mv -b")
                    .expect("\"ok\":", 10)

                    // 注册代币资源、发币
                    .sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Ddd::init -b")
                    .expect("\"ok\":", 10)
                    .sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Ddd::mint --arg 1000000000999u128 -b")
                    .expect("\"ok\":", 10)
                    .sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::init -b")
                    .expect("\"ok\":", 10)
                    .sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::mint --arg 1000000000999u128 -b")
                    .expect("\"ok\":", 10)

                    // ///////////////// usdx /////////////////
                    .sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Usdx::init -b")
                    .expect("\"ok\":", 10)
                    .sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Usdx::mint --arg 1500000000999u128 -b")
                    .expect("\"ok\":", 10)
                    // ///////////////// usdx /////////////////

                    // 转一部分币给账户2
                    .sendLine("account default 0xff2794187d72cc3a9240198ca98ac7b6")
                    .expect("\"ok\":", 10)
                    .sendLine("account unlock")
                    .expect("\"ok\":", 10)
                    .sendLine("account accept-token 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot -b")
                    .expect("\"ok\":", 10)
                    //.waitSeconds(10)// or -b
                    .sendLine("account accept-token 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Ddd::Ddd -b")
                    .expect("\"ok\":", 10)
                    //.waitSeconds(10)
                    .sendLine("account show")
                    .expect("\"ok\":", 10)
                    .sendLine("account default 0x598b8cbfd4536ecbe88aa1cfaffa7a62")
                    .expect("\"ok\":", 10)
                    .sendLine("account unlock")
                    .expect("\"ok\":", 10)
                    .sendLine("account show")
                    .expect("\"ok\":", 10)
                    .sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x1::TransferScripts::peer_to_peer_v2 -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot --arg 0xff2794187d72cc3a9240198ca98ac7b6 --arg 100000u128 -b")
                    .expect("\"ok\":", 10)
                    .sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x1::TransferScripts::peer_to_peer_v2 -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Ddd::Ddd --arg 0xff2794187d72cc3a9240198ca98ac7b6 --arg 100000u128 -b")
                    .expect("\"ok\":", 10)
                    // 注册交易对 Bot:Ddd
                    .sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapScripts::register_swap_pair -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Ddd::Ddd -b")
                    .expect("\"ok\":", 10)
                    // 注册 STC / BOT 交易对
                    .sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapScripts::register_swap_pair -t 0x1::STC::STC -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot -b")
                    .expect("\"ok\":", 10)
                    // 增加流动性 Bot:Ddd
                    .sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapScripts::add_liquidity -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Ddd::Ddd --arg 10000u128 --arg 100000u128 --arg 5000u128 --arg 50000u128 -b")
                    .expect("\"ok\":", 10)

                    // /////////// usdx ////////////////
                    // 注册交易对 Bot:Usdx
                    .sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapScripts::register_swap_pair -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Usdx::Usdx -b")
                    .expect("\"ok\":", 10)
            ;
            // //////////// TokenSwapGovScript::genesis_initialize ////////////
            commandLineInteractor.sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapGovScript::genesis_initialize -b ")
                    .expect("\"ok\":", 10);

            // ///////////////// Bot / Ddd /////////////////
            // ////////////// add_farm_pool, Bot:Ddd ////////////////
            commandLineInteractor.sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapFarmScript::add_farm_pool -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Ddd::Ddd --arg 10000u128 -b")
                    .expect("\"ok\":", 10);
            // /////////////// stake in farm, Bot:Ddd ///////////////
            commandLineInteractor.sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapFarmScript::stake -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Ddd::Ddd --arg 1000u128 -b")
                    .expect("\"ok\":", 10);
            // ///////////////// Bot / Ddd /////////////////

            // ///////////////// Bot / Usdx /////////////////
            // 增加流动性，Bot:Usdx
            commandLineInteractor.sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapScripts::add_liquidity -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Usdx::Usdx --arg 100000000000u128 --arg 150000000000u128 --arg 50000000000u128 --arg 75000000000u128 -b")
                    .expect("\"ok\":", 10);
            // //////////////// add_farm_pool, Bot:Usdx ////////////////
            commandLineInteractor.sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapFarmScript::add_farm_pool -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Usdx::Usdx --arg 10000u128 -b")
                    .expect("\"ok\":", 10);
            // //////////////// stake in farm, Bot:Usdx ////////////////
            commandLineInteractor.sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapFarmScript::stake -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Usdx::Usdx --arg 1000u128 -b")
                    .expect("\"ok\":", 10);
            // ///////////////// Bot / Usdx /////////////////

            // ///////////////// Ddd / Usdx /////////////////
            // 注册交易对 Ddd:Usdx
            commandLineInteractor
                    .sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapScripts::register_swap_pair -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Ddd::Ddd -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Usdx::Usdx -b")
                    .expect("\"ok\":", 10)
            ;
            // 增加流动性，Ddd:Usdx
            commandLineInteractor.sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapScripts::add_liquidity -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Ddd::Ddd -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Usdx::Usdx --arg 100000000000u128 --arg 15000000000u128 --arg 50000000000u128 --arg 7500000000u128 -b")
                    .expect("\"ok\":", 10);
            // ///////////////// Ddd / Usdx /////////////////

            // ///////////////// TBD / Usdx /////////////////
            // 注册交易对 TBD:Usdx
            commandLineInteractor
                    .sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapScripts::register_swap_pair -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TBD::TBD -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Usdx::Usdx -b")
                    .expect("\"ok\":", 10)
            ;
            // 增加流动性，TBD:Usdx
            commandLineInteractor.sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapScripts::add_liquidity -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TBD::TBD -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Usdx::Usdx --arg 1000u128 --arg 1500u128 --arg 500u128 --arg 750u128 -b")
                    .expect("\"ok\":", 10);
            // ///////////////// TBD / Usdx /////////////////

            // ///////////////// TBD /////////////////
            // # 等待N个时间
            //dev sleep -t 3600000
            //dev gen-block
            // ///////////////////////////
            // 查看 farm 奖励，Bot:Ddd
            commandLineInteractor.sendLine("dev call --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapFarmScript::lookup_gain -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Ddd::Ddd --arg 0x598b8cbfd4536ecbe88aa1cfaffa7a62")
                    .expect("\"ok\":", 10);
            // 领取 farm 奖励（TBD），Bot:Ddd。金额 0 为领取全部。
            commandLineInteractor.sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapFarmScript::harvest -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Ddd::Ddd -b --arg 0u128")
                    .expect("\"ok\":", 10);
            // ///////////////// TBD /////////////////

        } // ---------------- end of init. -------------------

        // unlock account
        commandLineInteractor.sendLine("account default 0x598b8cbfd4536ecbe88aa1cfaffa7a62").expect("\"ok\":", 10).sendLine("account unlock").expect("\"ok\":", 10);

//        // 查看 farm 奖励，Bot:Ddd
//        commandLineInteractor.sendLine("dev call --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapFarmScript::lookup_gain -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Ddd::Ddd --arg 0x598b8cbfd4536ecbe88aa1cfaffa7a62")
//                .expect("\"ok\":", 10);
        if (true) return;

        // 查询当前用户流动性
        commandLineInteractor.sendLine("dev call --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapRouter::liquidity -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Ddd::Ddd --arg 0x598b8cbfd4536ecbe88aa1cfaffa7a62")
                .expect("\"ok\":", 10);

        // unstake, Bot:Ddd
        commandLineInteractor.sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapFarmScript::unstake -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Ddd::Ddd -b")
                .expect("\"ok\":", 10);
        // stake in farm, Bot:Ddd
        commandLineInteractor.sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapFarmScript::stake -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Ddd::Ddd --arg 1000u128 -b")
                .expect("\"ok\":", 10);

        if (true) return;

        commandLineInteractor
                // 查询整体流动性
                .sendLine("dev call --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapRouter::total_liquidity -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Ddd::Ddd")
                .expect("\"ok\":", 10)
                // 查询swap pair存在性
                .sendLine("dev call --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapRouter::swap_pair_exists -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Ddd::Ddd")
                .expect("\"ok\":", 10)
                // 查询当前用户流动性
                .sendLine("dev call --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapRouter::liquidity -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Ddd::Ddd --arg 0x598b8cbfd4536ecbe88aa1cfaffa7a62")
                .expect("\"ok\":", 10)
                // 置换代币
                .sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapScripts::swap_exact_token_for_token -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Ddd::Ddd --arg 100u128 --arg 100u128 -b")
                .expect("\"ok\":", 10)
                // 获取代币存量
                .sendLine("dev call --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapRouter::get_reserves -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Ddd::Ddd")
                .expect("\"ok\":", 10)
                // 获取代币换出额度
                .sendLine("dev call --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapRouter::get_amount_out --arg 1000u128 --arg 1000u128 --arg 2000u128")
                .expect("\"ok\":", 10)
                // 获取流动性计算
                .sendLine("dev call --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapRouter::quote --arg 1000u128 --arg 1000u128 --arg 2000u128")
                .expect("\"ok\":", 10)
                // 移除流动性
                .sendLine("account execute-function -s 0x598b8cbfd4536ecbe88aa1cfaffa7a62 --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwapScripts::remove_liquidity -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Ddd::Ddd --arg 1000u128 --arg 1u128 --arg 1u128 -b")
                .expect("\"ok\":", 10)
                // 检查代币发布
                .sendLine("dev call --function 0x598b8cbfd4536ecbe88aa1cfaffa7a62::TokenSwap::assert_is_token -t 0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot")
                .expect("\"ok\":", 10)
        ;
    }

    private static String[] deployMoveCodeCommands() {
        return ("dev deploy storage/0x598b8cbfd4536ecbe88aa1cfaffa7a62/modules/TBD.mv -b\n" +
                "dev deploy storage/0x598b8cbfd4536ecbe88aa1cfaffa7a62/modules/YieldFarming.mv -b\n" +
                "dev deploy storage/0x598b8cbfd4536ecbe88aa1cfaffa7a62/modules/TokenSwap.mv -b\n" +
                "dev deploy storage/0x598b8cbfd4536ecbe88aa1cfaffa7a62/modules/TokenSwapRouter.mv -b\n" +
                "dev deploy storage/0x598b8cbfd4536ecbe88aa1cfaffa7a62/modules/TokenSwapScripts.mv -b\n" +
                "dev deploy storage/0x598b8cbfd4536ecbe88aa1cfaffa7a62/modules/TokenSwapGovPoolType.mv -b\n" +
                "dev deploy storage/0x598b8cbfd4536ecbe88aa1cfaffa7a62/modules/TokenSwapFarm.mv -b\n" +
                "dev deploy storage/0x598b8cbfd4536ecbe88aa1cfaffa7a62/modules/TokenSwapGov.mv -b\n" +
                "dev deploy storage/0x598b8cbfd4536ecbe88aa1cfaffa7a62/modules/TokenSwapFarmRouter.mv -b\n" +
                "dev deploy storage/0x598b8cbfd4536ecbe88aa1cfaffa7a62/modules/TokenSwapFarmScript.mv -b\n" +
                "dev deploy storage/0x598b8cbfd4536ecbe88aa1cfaffa7a62/modules/TokenSwapGovScript.mv -b"
        ).split("\\n");
    }
}
