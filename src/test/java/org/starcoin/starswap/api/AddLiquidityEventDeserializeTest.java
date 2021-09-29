package org.starcoin.starswap.api;

import com.novi.serde.DeserializationError;
import org.starcoin.starswap.types.AddLiquidityEvent;
import org.starcoin.utils.CommonUtils;


public class AddLiquidityEventDeserializeTest {

    public static void main(String[] args) {
        String eventHex = "0x6c76000000000000000000000000000007fa08a855753f0ff7292fdcbe871216034464640344646407fa08a855753f0ff7292fdcbe87121603426f7403426f7407fa08a855753f0ff7292fdcbe871216";
        byte[] eventBytes = CommonUtils.hexToByteArray(eventHex);
        try {
            AddLiquidityEvent addLiquidityEvent = AddLiquidityEvent.bcsDeserialize(eventBytes);
            System.out.println(addLiquidityEvent);
            System.out.println(CommonUtils.byteListToHexWithPrefix(addLiquidityEvent.signer.value));
            System.out.println(CommonUtils.byteListToHexWithPrefix(addLiquidityEvent.x_token_code.address.value));
            System.out.println(addLiquidityEvent.x_token_code.module);
            System.out.println(addLiquidityEvent.x_token_code.name);
            System.out.println(CommonUtils.byteListToHexWithPrefix(addLiquidityEvent.y_token_code.address.value));
            System.out.println(addLiquidityEvent.y_token_code.module);
            System.out.println(addLiquidityEvent.y_token_code.name);
            System.out.println(addLiquidityEvent.liquidity);

        } catch (DeserializationError deserializationError) {
            deserializationError.printStackTrace();
        }
    }
}
