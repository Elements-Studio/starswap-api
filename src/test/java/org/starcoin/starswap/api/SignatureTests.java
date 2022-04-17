package org.starcoin.starswap.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.starcoin.starswap.api.utils.SignatureUtils;
import org.starcoin.utils.HexUtils;

import java.util.HashMap;
import java.util.Map;

public class SignatureTests {

    private final String testPrivateKey = "";
    private final String testPublicKey = "0x65b8da978ee094f6091b5dfe6f707a1911a4015c0211cd419a2c289dd8692d20";

    @Test
    public void testEd25519Sign() {
        Map<String, String> m = new HashMap<>();
        String[] addresses = new String[]{"0x18351d311d32201149a4df2a9fc2db8a"};
        for (String address : addresses) {
            byte[] sig = SignatureUtils.ed25519Sign(HexUtils.hexToByteArray(testPrivateKey),
                    HexUtils.hexToByteArray(address));
            m.put(address, "0x" + HexUtils.byteArrayToHex(sig));
//            boolean ok = SignatureUtils.ed25519Verify(HexUtils.hexToByteArray(testPublicKey),
//                    HexUtils.hexToByteArray(addr), sig
//            );
//            System.out.println(ok);
        }
        try {
            String j = new ObjectMapper().writeValueAsString(m);
            System.out.println(j);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }
}
