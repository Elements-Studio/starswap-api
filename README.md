# Statswap API

## API 说明

以下说明的 URL 示例的 `BASE_URL` 为 `http://localhost:8600/barnard`，在使用时请将其替换为实际的 `BASE_URL`。

取 Token 的列表：

```
http://localhost:8600/barnard/v1/tokens
```

取一个 Token 的的详细信息（假设 TokenId 为 `Bot`）：

```
http://localhost:8600/barnard/v1/tokens/Bot
```

取 Liquidity Token 的列表：

```
http://localhost:8600/barnard/v1/liquidityTokens
```

取一个 Liquidity Token 的信息（目前一个 Token Pair 只支持对应一个 Liquidity Token，故可以使用参数 `{TokenId_X}:{TokenId_Y}` 获取）：

```
http://localhost:8600/barnard/v1/liquidityTokens/Bot:Ddd
```

获取流动性池子的列表：

```
http://localhost:8600/barnard/v1/liquidityPools
```

获得某个流动性池子的信息（目前一个 Token Pair 只支持一个池子，故可以使用参数 `{TokenId_X}:{TokenId_Y}` 获取）：

```
http://localhost:8600/barnard/v1/liquidityPools/Bot:Ddd
```

取得某个账号地址注入的流动性列表：

```
http://localhost:8600/barnard/v1/liquidityAccounts?accountAddress=0x598b8cbfd4536ecbe88aa1cfaffa7a62
```

获取多个币对的流动性池子的 Reserves 的列表（目前一个 Token Pair 只支持一个池子）：

```shell
curl --header 'Content-Type: application/json' -d '[["0x00000000000000000000000000000001::STC::STC","0xfe125d419811297dfab03c61efec0bc9::FAI::FAI"],["0x00000000000000000000000000000001::STC::STC","0x4783d08fb16990bd35d83f3e23bf93b8::STAR::STAR"],["0x00000000000000000000000000000001::STC::STC","0x88e2677b89841cd4ee7c15535798e1c8::WEN::WEN"],["0x00000000000000000000000000000001::STC::STC","0x88e2677b89841cd4ee7c15535798e1c8::SHARE::SHARE"]]' http://localhost:8600/barnard/v1/getTokenPairReservesList
```

返回结果类似：

```json
[[7708928910789,98759183446],[11209819318991,110342239475554],[22901598372664,2024315245868],[19729297654058,535292102659369]]
```

取得兑换的最佳路径，输入换入代币的金额，估算换出代币金额：

```
http://localhost:8600/barnard/v1/getBestSwapPath?from=Bot&to=Ddd&amount=10000
```

输出结果类似：

```json
{"path":["Bot","Usdx","Ddd"],"amountOut":99394}
```

取得兑换的最佳路径，输入预期换出代币的金额，估算需要换入的代币金额：

```
http://localhost:8600/barnard/v1/getBestSwapPathAndAmountIn?from=Bot&to=Ddd&amountOut=10000
```

输出结果类似：

```json
{"path":["Bot","Usdx","Ddd"],"amountIn":1007}
```

取得 Farm 的列表：

```
http://localhost:8600/barnard/v1/lpTokenFarms
```

取得某个 Farm 的信息：

```
http://localhost:8600/barnard/v1/lpTokenFarms/Bot:Ddd
```

取得某个账号地址参与抵押的 Farm 的列表：

```
http://localhost:8600/barnard/v1/lpTokenFarmAccounts?accountAddress=0x598b8cbfd4536ecbe88aa1cfaffa7a62
```

取得账号在 Farm 中的抵押信息：

```
http://localhost:8600/barnard/v1/getAccountFarmStakeInfo?tokenXId=STC&tokenYId=STAR&accountAddress=0x598b8cbfd4536ecbe88aa1cfaffa7a62
```

该接口返回：账号抵押的流动性的数额，Farm 的总抵押流动性的数额，账号在 farm 中的占比，账号抵押的流动性的美元计价的金额，抵押的流动性可以换回的 Token 的数额等。


取得所有 Farm 的 TVL：

```
http://localhost:8600/barnard/v1/farmingTvlInUsd
```

取得 syrup pool 的列表：

```
http://localhost:8600/barnard/v1/syrupPools
```

取得某个 syrup pool 的信息（目前一个 Token 只支持一个池子，故可以使用参数 `{TokenId}` 获取，比如获取 STAR 的 syrup pool）：

```
http://localhost:8600/barnard/v1/syrupPools/STAR
```

取得所有 syrup pool 的 TVL：

```
http://localhost:8600/barnard/v1/syrupPoolTvlInUsd
```


取得某个账号地址参与抵押的 syrup pool 的列表：

```
http://localhost:8600/barnard/v1/syrupPoolAccounts?accountAddress=0x4783d08fb16990bd35d83f3e23bf93b8
```

取得某个账号某种代币在 syrup pool 的抵押列表：

```
http://localhost:8600/barnard/v1/syrupStakes?accountAddress=0x4783d08fb16990bd35d83f3e23bf93b8&tokenId=STAR
```

查询账号的 veStar 数量：

```
http://localhost:8600/barnard/v1/getAccountVeStarAmount?accountAddress=0x598b8cbfd4536ecbe88aa1cfaffa7a62
```

取得 Token 到其兑换美元的价格的 Pair 之间的映射信息（用于调用价格 API）：

```
http://localhost:8600/barnard/v1/tokenToUsdPricePairMappings
```

Get token to USD exchange rate computed by swap pool:

```
http://localhost:8600/barnard/v1/getToUsdExchangeRate?t=STAR
```

### 价格相关 API

取得某个 token 最接近某个时间点的兑美元的价格：

```
http://localhost:8600/barnard/v1/price-api/getProximateToUsdPriceRound?token=0x00000000000000000000000000000001::STC::STC&timestamp=1
```

取得一个或多个 token 最接近某个时间点的兑美元的价格（查询参数名 `t` 可以重复出现，对无法取得兑美元价格的 token 返回结果为 null）：

```
http://localhost:8600/barnard/v1/price-api/getProximateToUsdPriceRounds?t=0x00000000000000000000000000000001::STC::STC&t=0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot&t=0x9350502a3af6c617e9a42fa9e306a385::BX_USDT::BX_USDT&timestamp=1630998680316
```

取得一个或多个 token 在多个时间点的最接近时间点兑美元的价格（查询参数名 `t` 和 `timestamp` 可以重复出现。这个接口相当于 `getProximateToUsdPriceRounds` 的批量调用接口。返回的 JSON 对象的 Key 是 timestamp，对无法取得兑美元价格的 token 返回结果为 null）：

```
http://localhost:8600/barnard/v1/price-api/getMultiTimestampProximateToUsdPriceRounds?t=0x00000000000000000000000000000001::STC::STC&timestamp=1630998680316&&timestamp=1630999680996&timestamp=1631098680316
```

取得一个或多个 token 兑美元的涨跌幅信息（对无法取得涨跌幅信息的 token 返回结果为 null）：

```
http://localhost:8600/barnard/v1/price-api/getToUsdPriceGrowths?t=0x00000000000000000000000000000001::STC::STC&t=0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot
```

取得某个 token 兑美元的当前汇率：

```
http://localhost:8600/barnard/v1/price-api/getToUsdExchangeRate?token=0x00000000000000000000000000000001::STC::STC
```

取得查询参数 `t` 列出的任一个 token 兑美元的当前汇率：

```
http://localhost:8600/barnard/v1/price-api/getAnyToUsdExchangeRate?t=0x00000000000000000000000000000001::STC::STC&t=0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot
```

取得查询参数 `t` 列出的任一个 token 最接近某个时间点（timestamp）的兑美元的汇率：

> 注意，当前系统没有保存**通过 swap 池子计算的**币对价格的历史记录，所以部分代币虽然可以通过接口获取兑美元的当前价格，但是不能通过本接口获取兑美元的历史价格。

```
http://localhost:8600/barnard/v1/price-api/getAnyProximateToUsdExchangeRate?t=0x00000000000000000000000000000001::STC::STC&t=0x598b8cbfd4536ecbe88aa1cfaffa7a62::Bot::Bot&timestamp=1630998680316
```

---

更多 API 见 Swagger UI：

```
http://localhost:8600/barnard/swagger-ui/index.html
```

## 代码说明

### 数据结构的序列化/反序列化

参考链接：

* https://crates.io/crates/serde-generate/0.9.0
* https://lib.rs/crates/bcs
* https://github.com/novifinancial/serde-reflection/blob/master/serde-generate/README.md#quick-start-with-python-and-bincode

