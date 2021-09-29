package org.starcoin.starswap.api.service;

import com.novi.serde.DeserializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.starcoin.bean.Event;
import org.starcoin.starswap.api.data.model.*;
import org.starcoin.starswap.types.AddFarmEvent;
import org.starcoin.starswap.types.AddLiquidityEvent;
import org.starcoin.starswap.types.StakeEvent;
import org.starcoin.utils.CommonUtils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class HandleEventService {

    private static final Logger LOG = LoggerFactory.getLogger(HandleEventService.class);

    private final LiquidityAccountService liquidityAccountService;

    private final TokenService tokenService;

    private final LiquidityTokenService liquidityTokenService;

    private final LiquidityTokenFarmAccountService liquidityTokenFarmAccountService;

    private final LiquidityTokenFarmService liquidityTokenFarmService;

    private final NodeHeartbeatService nodeHeartbeatService;

    public HandleEventService(@Autowired LiquidityAccountService liquidityAccountService,
                              @Autowired TokenService tokenService,
                              @Autowired LiquidityTokenService liquidityTokenService,
                              @Autowired LiquidityTokenFarmService liquidityTokenFarmService,
                              @Autowired LiquidityTokenFarmAccountService liquidityTokenFarmAccountService,
                              @Autowired NodeHeartbeatService nodeHeartbeatService) {
        this.liquidityAccountService = liquidityAccountService;
        this.tokenService = tokenService;
        this.liquidityTokenService = liquidityTokenService;
        this.liquidityTokenFarmService = liquidityTokenFarmService;
        this.liquidityTokenFarmAccountService = liquidityTokenFarmAccountService;
        this.nodeHeartbeatService = nodeHeartbeatService;
    }

    private static TypeTagStruct tryParseTypeTagStruct(String s) {
        // TypeTag example:
        // 0x00000000000000000000000000000001::Oracle::OracleUpdateEvent<0x598b8cbfd4536ecbe88aa1cfaffa7a62::YFI_USD::YFI_USD, u128>
        String[] fs = s.split("::", 3);
        if (fs.length != 3) {
            return null;
        }
        TypeTagStruct t = new TypeTagStruct();
        t.setAddress(fs[0]);
        t.setModule(fs[1]);
        int idxOfLT = fs[2].indexOf("<");
        if (idxOfLT == -1) {
            t.setName(fs[2]);
        } else {
            t.setName(fs[2].substring(0, idxOfLT));
            //System.out.println("----------- parsed struct tag: " + t);
            //todo parse type params???
        }
        return t;
    }

    // token_code={addr=0x598b8cbfd4536ecbe88aa1cfaffa7a62, module_name=0x426f74, name=0x426f74}
    public static StructType tokenCodeMapToStructType(Map<String, Object> m) {
        String addr = m.get("addr").toString();
        String moduleName = m.get("module_name").toString();
        String name = m.get("name").toString();
        if (moduleName.startsWith("0x")) {
            moduleName = new String(CommonUtils.hexToByteArray(moduleName), StandardCharsets.UTF_8);
        }
        if (name.startsWith("0x")) {
            name = new String(CommonUtils.hexToByteArray(name), StandardCharsets.UTF_8);
        }
        return new StructType(addr, moduleName, name);
    }

    public void handleEvent(Event event, String eventFromAddress) {
        TypeTagStruct eventTypeTagStruct = tryParseTypeTagStruct(event.getTypeTag());
        if (eventTypeTagStruct == null) {
            return;
        }
        boolean eventHandled;
        try {
            if ("TokenSwap".equals(eventTypeTagStruct.getModule()) && "AddLiquidityEvent".equals(eventTypeTagStruct.getName())) {
                handleAddLiquidityEvent(event, eventFromAddress, eventTypeTagStruct);
            } else if ("TokenSwapFarm".equals(eventTypeTagStruct.getModule()) && "AddFarmEvent".equals(eventTypeTagStruct.getName())) {
                handleAddFarmEvent(event, eventFromAddress, eventTypeTagStruct);
            } else if ("TokenSwapFarm".equals(eventTypeTagStruct.getModule()) && "StakeEvent".equals(eventTypeTagStruct.getName())) {
                handleStakeEvent(event, eventFromAddress, eventTypeTagStruct);
            } else {
                throw new RuntimeException("Unknown event type.");
            }
            eventHandled = true;
        } catch (RuntimeException runtimeException) {
            LOG.error("Handle event error. event: " + event, runtimeException);
            eventHandled = false;
        }

        try {
            if (eventHandled) {
                nodeHeartbeatService.beat(new BigInteger(event.getBlockNumber()));
            } else {
                nodeHeartbeatService.reset();
            }
        } catch (RuntimeException runtimeException) {
            LOG.error("Save heartbeat in database error.", runtimeException);
        }
    }

    @SuppressWarnings("unchecked")
    private void handleStakeEvent(Event event, String eventFromAddress, TypeTagStruct eventTypeTagStruct) {
        // decodeEventData={
        // x_token_code={addr=0x598b8cbfd4536ecbe88aa1cfaffa7a62, module_name=0x426f74, name=0x426f74},
        // y_token_code={addr=0x598b8cbfd4536ecbe88aa1cfaffa7a62, module_name=0x446464, name=0x446464},
        // signer=0x598b8cbfd4536ecbe88aa1cfaffa7a62,
        // amount=1000,
        // admin=0x598b8cbfd4536ecbe88aa1cfaffa7a62
        // }
//        StructType xTokenType = tokenCodeMapToStructType((Map<String, Object>) event.getDecodeEventData().get("x_token_code"));
//        StructType yTokenType = tokenCodeMapToStructType((Map<String, Object>) event.getDecodeEventData().get("y_token_code"));
//        String xTokenTypeAddress = xTokenType.getAddress();
//        String xTokenTypeModule = xTokenType.getModule();
//        String xTokenTypeName = xTokenType.getName();
//        String yTokenTypeAddress = yTokenType.getAddress();
//        String yTokenTypeModule = yTokenType.getModule();
//        String yTokenTypeName = yTokenType.getName();
//        String accountAddress = event.getDecodeEventData().get("signer").toString();

        // /////////////////////////////////////
        String eventData = event.getData();
        StakeEvent stakeEvent;
        try {
            stakeEvent = StakeEvent.bcsDeserialize(CommonUtils.hexToByteArray(eventData));
        } catch (DeserializationError deserializationError) {
            throw new RuntimeException("StakeEvent.bcsDeserialize error.", deserializationError);
        }
        String xTokenTypeAddress = CommonUtils.byteListToHexWithPrefix(stakeEvent.x_token_code.address.value);
        String xTokenTypeModule = stakeEvent.x_token_code.module;
        String xTokenTypeName = stakeEvent.x_token_code.name;
        String yTokenTypeAddress = CommonUtils.byteListToHexWithPrefix(stakeEvent.y_token_code.address.value);
        String yTokenTypeModule = stakeEvent.y_token_code.module;
        String yTokenTypeName = stakeEvent.y_token_code.name;
        String accountAddress = CommonUtils.byteListToHexWithPrefix(stakeEvent.signer.value);

        Token xToken = tokenService.getTokenByStructType(xTokenTypeAddress, xTokenTypeModule, xTokenTypeName);
        if (xToken == null) {
            throw new RuntimeException("Cannot get token by struct type: " + stakeEvent.x_token_code);
        }
        Token yToken = tokenService.getTokenByStructType(yTokenTypeAddress, yTokenTypeModule, yTokenTypeName);
        if (yToken == null) {
            throw new RuntimeException("Cannot get token by struct type: " + stakeEvent.y_token_code);
        }
        String tokenXId = xToken.getTokenId();
        String tokenYId = yToken.getTokenId();
        // 通过查询得到 LiquidityToken 的地址
        LiquidityToken liquidityToken = liquidityTokenService.findOneByTokenIdPair(tokenXId, tokenYId);
        if (liquidityToken == null) {
            throw new RuntimeException("Cannot find LiquidityToken by tokenId pair: " + tokenXId + " / " + tokenYId);
        }
        String liquidityTokenAddress = liquidityToken.getLiquidityTokenId().getLiquidityTokenAddress();
        LiquidityTokenId liquidityTokenId = new LiquidityTokenId(tokenXId, tokenYId, liquidityTokenAddress);
        String farmAddress = eventFromAddress;// note：目前 Farm 池子的地址就是 event 的来源地址！
        LiquidityTokenFarmId liquidityTokenFarmId = new LiquidityTokenFarmId(liquidityTokenId, farmAddress);
        // 如果只是查询 LiquidityToken 的信息，那么数据库中没有 Farm 的信息也可以正常执行。
//        LiquidityTokenFarm liquidityTokenFarm = liquidityTokenFarmService.findOneByTokenIdPair(tokenXId, tokenYId);
//        LiquidityTokenFarmId liquidityTokenFarmId = liquidityTokenFarm.getLiquidityTokenFarmId();
//        if (!eventFromAddress.equalsIgnoreCase(liquidityTokenFarmId.getFarmAddress())) {
//            throw new RuntimeException("eventFromAddress != farmAddress from findOneByTokenIdPair");
//        }
        LiquidityTokenFarmAccountId farmAccountId = new LiquidityTokenFarmAccountId(accountAddress, liquidityTokenFarmId);
        liquidityTokenFarmAccountService.activeFarmAccount(farmAccountId);
    }

    @SuppressWarnings("unchecked")
    private void handleAddFarmEvent(Event event, String eventFromAddress, TypeTagStruct eventTypeTagStruct) {
        //System.out.println(event);
        //decodeEventData={
        // x_token_code={addr=0x598b8cbfd4536ecbe88aa1cfaffa7a62, module_name=0x55736478, name=0x55736478},
        // y_token_code={addr=0x598b8cbfd4536ecbe88aa1cfaffa7a62, module_name=0x426f74, name=0x426f74},
        // signer=0x598b8cbfd4536ecbe88aa1cfaffa7a62,
        // admin=0x598b8cbfd4536ecbe88aa1cfaffa7a62
        // }
        //StructType xTokenType = tokenCodeMapToStructType((Map<String, Object>) event.getDecodeEventData().get("x_token_code"));
        //StructType yTokenType = tokenCodeMapToStructType((Map<String, Object>) event.getDecodeEventData().get("y_token_code"));
//        String xTokenTypeAddress = xTokenType.getAddress();
//        String xTokenTypeModule = xTokenType.getModule();
//        String xTokenTypeName = xTokenType.getName();
//        String yTokenTypeAddress = yTokenType.getAddress();
//        String yTokenTypeModule = yTokenType.getModule();
//        String yTokenTypeName = yTokenType.getName();

        String eventData = event.getData();
        AddFarmEvent addFarmEvent;
        try {
            addFarmEvent = AddFarmEvent.bcsDeserialize(CommonUtils.hexToByteArray(eventData));
        } catch (DeserializationError deserializationError) {
            throw new RuntimeException("AddFarmEvent.bcsDeserialize error.", deserializationError);
        }
        //System.out.println(addLiquidityEvent);
        String xTokenTypeAddress = CommonUtils.byteListToHexWithPrefix(addFarmEvent.x_token_code.address.value);
        String xTokenTypeModule = addFarmEvent.x_token_code.module;
        String xTokenTypeName = addFarmEvent.x_token_code.name;
        String yTokenTypeAddress = CommonUtils.byteListToHexWithPrefix(addFarmEvent.y_token_code.address.value);
        String yTokenTypeModule = addFarmEvent.y_token_code.module;
        String yTokenTypeName = addFarmEvent.y_token_code.name;
        //String accountAddress = CommonUtils.byteListToHexWithPrefix(addFarmEvent.signer.value);

        Token xToken = tokenService.getTokenByStructType(xTokenTypeAddress, xTokenTypeModule, xTokenTypeName);
        if (xToken == null) {
            throw new RuntimeException("Cannot get token by struct type: " + addFarmEvent.x_token_code);
        }
        Token yToken = tokenService.getTokenByStructType(yTokenTypeAddress, yTokenTypeModule, yTokenTypeName);
        if (yToken == null) {
            throw new RuntimeException("Cannot get token by struct type: " + addFarmEvent.y_token_code);
        }
        String farmAddress = eventFromAddress;// note: 目前 Farm 池子的地址就是 event 的来源地址！
        String tokenXId = xToken.getTokenId();
        String tokenYId = yToken.getTokenId();
        LiquidityToken liquidityToken = liquidityTokenService.findOneByTokenIdPair(tokenXId, tokenYId);
        if (liquidityToken == null) {
            throw new RuntimeException("Cannot find LiquidityToken by tokenId pair: " + tokenXId + " / " + tokenYId);
        }
        String liquidityTokenAddress = liquidityToken.getLiquidityTokenId().getLiquidityTokenAddress();
        LiquidityTokenId liquidityTokenId = new LiquidityTokenId(tokenXId, tokenYId, liquidityTokenAddress);
        LiquidityTokenFarmId liquidityTokenFarmId = new LiquidityTokenFarmId(liquidityTokenId, farmAddress);

        LiquidityTokenFarm liquidityTokenFarm = new LiquidityTokenFarm();
        liquidityTokenFarm.setLiquidityTokenFarmId(liquidityTokenFarmId);
        liquidityTokenFarmService.addFarm(liquidityTokenFarmId);
    }

    @SuppressWarnings("unchecked")
    private void handleAddLiquidityEvent(Event event, String eventFromAddress, TypeTagStruct eventTypeTagStruct) {
        String eventData = event.getData();
        AddLiquidityEvent addLiquidityEvent;
        try {
            addLiquidityEvent = AddLiquidityEvent.bcsDeserialize(CommonUtils.hexToByteArray(eventData));
        } catch (DeserializationError deserializationError) {
            throw new RuntimeException("AddLiquidityEvent.bcsDeserialize error.", deserializationError);
        }
        String xTokenTypeAddress = CommonUtils.byteListToHexWithPrefix(addLiquidityEvent.x_token_code.address.value);
        String xTokenTypeModule = addLiquidityEvent.x_token_code.module;
        String xTokenTypeName = addLiquidityEvent.x_token_code.name;
        String yTokenTypeAddress = CommonUtils.byteListToHexWithPrefix(addLiquidityEvent.y_token_code.address.value);
        String yTokenTypeModule = addLiquidityEvent.y_token_code.module;
        String yTokenTypeName = addLiquidityEvent.y_token_code.name;
        String accountAddress = CommonUtils.byteListToHexWithPrefix(addLiquidityEvent.signer.value);
        BigInteger liquidity = addLiquidityEvent.liquidity;

        // /////////////////////////////////////
//        StructType xTokenType = tokenCodeMapToStructType((Map<String, Object>) event.getDecodeEventData().get("x_token_code"));
//        StructType yTokenType = tokenCodeMapToStructType((Map<String, Object>) event.getDecodeEventData().get("y_token_code"));
//        String accountAddress = event.getDecodeEventData().get("signer").toString();
//        String xTokenTypeAddress = xTokenType.getAddress();
//        String xTokenTypeModule = xTokenType.getModule();
//        String xTokenTypeName = xTokenType.getName();
//        String yTokenTypeAddress = yTokenType.getAddress();
//        String yTokenTypeModule = yTokenType.getModule();
//        String yTokenTypeName = yTokenType.getName();
//        BigInteger liquidity = new BigInteger(event.getDecodeEventData().get("liquidity").toString());
        // /////////////////////////////////////

        Token xToken = tokenService.getTokenByStructType(xTokenTypeAddress, xTokenTypeModule, xTokenTypeName);
        if (xToken == null) {
            throw new RuntimeException("Cannot get token by struct type: " + addLiquidityEvent.x_token_code);
        }
        Token yToken = tokenService.getTokenByStructType(yTokenTypeAddress, yTokenTypeModule, yTokenTypeName);
        if (yToken == null) {
            throw new RuntimeException("Cannot get token by struct type: " + addLiquidityEvent.y_token_code);
        }
        String eventStructAddress = eventTypeTagStruct.getAddress();
        String liquidityTokenAddress = eventStructAddress; // note：目前 LiquidityToken 的地址就是事件的结构的地址！
        String liquidityPollAddress = eventFromAddress;// note：目前流动性池子的地址就是 event 的来源地址！
        LiquidityAccountId liquidityAccountId = new LiquidityAccountId(accountAddress,
                new LiquidityPoolId(
                        new LiquidityTokenId(xToken.getTokenId(), yToken.getTokenId(), liquidityTokenAddress),
                        liquidityPollAddress));
        this.liquidityAccountService.activeLiquidityAccount(liquidityAccountId);
    }

    public static class TypeTagStruct {
        /*
              "type_tag": {
              "Struct": {
                "address": "0x00000000000000000000000000000001",
                "module": "Account",
                "name": "DepositEvent",
                "type_params": []
              }
            },
         */

        String address;
        String module;
        String name;

        //@JsonProperty("type_params")
        List<Object> typeParams; // Is ok???

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getModule() {
            return module;
        }

        public void setModule(String module) {
            this.module = module;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Object> getTypeParams() {
            return typeParams;
        }

        public void setTypeParams(List<Object> typeParams) {
            this.typeParams = typeParams;
        }

        @Override
        public String toString() {
            return "StructTag{" +
                    "address='" + address + '\'' +
                    ", module='" + module + '\'' +
                    ", name='" + name + '\'' +
                    ", typeParams=" + typeParams +
                    '}';
        }
    }
}
