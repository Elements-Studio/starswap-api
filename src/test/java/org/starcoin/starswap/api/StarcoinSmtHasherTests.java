package org.starcoin.starswap.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novi.serde.DeserializationError;
import com.novi.serde.SerializationError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.starcoin.bean.StateWithProof;
import org.starcoin.smt.*;
import org.starcoin.starswap.api.utils.JsonRpcClient;
import org.starcoin.types.*;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StarcoinSmtHasherTests {
    /*
curl --location --request POST 'https://main-seed.starcoin.org' \
--header 'Content-Type: application/json' \
--data-raw '{
 "id":101,
 "jsonrpc":"2.0",
 "method":"state.get_with_proof_by_root",
 "params":["{ACCOUNT_ADDRESS}/1/0x8c109349c6bd91411d6bc962e080c4a3::TokenSwapFarmBoost::UserInfo<0x00000000000000000000000000000001::STC::STC, 0x8c109349c6bd91411d6bc962e080c4a3::STAR::STAR>", "0x99163c0fc319b62c3897ada8f97881e396e33b30383f47e23d93aaed07d6806d"]
}'
     */

    //return state:

    /*
    "state": "0xfa000000000000007b161ceeef010000000000000000000000000000000000000000000000000000"
     */

    //move code:

    /*
    struct UserInfo<phantom X, phantom Y> has key, store {
        boost_factor: u64,
        locked_vetoken: VToken<VESTAR>,
        user_amount: u128,
    }
     */

    @Test
    public void testGetStateWithProofByRoot() {
        JsonRpcClient jsonRpcClient = null;
        try {
            jsonRpcClient = new JsonRpcClient("https://main-seed.starcoin.org");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String testAccountAddress = "0x76a45fbf9631f68eb09812a21452e38e";
        StateWithProof stateWithProof = jsonRpcClient.getStateWithProofByRoot(
                testAccountAddress + "/1/0x8c109349c6bd91411d6bc962e080c4a3::TokenSwapFarmBoost::UserInfo<0x00000000000000000000000000000001::STC::STC, 0x8c109349c6bd91411d6bc962e080c4a3::STAR::STAR>",
                "0x99163c0fc319b62c3897ada8f97881e396e33b30383f47e23d93aaed07d6806d");
        System.out.println(stateWithProof);
        try {
            System.out.println(new ObjectMapper().writeValueAsString(stateWithProof));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String stateHex = stateWithProof.getState();
        //byte[] stateBytes = HexUtils.hexToByteArray(stateHex);
        System.out.println(stateHex);
    }

    @Test
    void testDataPathHash() throws SerializationError {
//        byte[] key = HexUtils.hexToByteArray("{ACCOUNT_ADDRESS}");
//        StarcoinTreeHasher th = new StarcoinTreeHasher();
//        Bytes keyHash = th.path(new Bytes(key));
//        System.out.println(HexUtils.byteArrayToHex(keyHash.getValue()));
//        //a64823bd1efeac7ef50fb0fb5d5cb142823e4029d4da013d06e51b86deef73c8

        List<TypeTag> typeParams = new ArrayList<>();
        StructTag innerStructTag1 = new StructTag(AccountAddress.valueOf(HexUtils.hexToByteArray("0x00000000000000000000000000000001")),
                new Identifier("STC"), new Identifier("STC"), Collections.emptyList());
        StructTag innerStructTag2 = new StructTag(AccountAddress.valueOf(HexUtils.hexToByteArray("0x8c109349c6bd91411d6bc962e080c4a3")),
                new Identifier("STAR"), new Identifier("STAR"), Collections.emptyList());
        typeParams.add(new TypeTag.Struct(innerStructTag1));
        typeParams.add(new TypeTag.Struct(innerStructTag2));
        StructTag structTag = new StructTag(AccountAddress.valueOf(HexUtils.hexToByteArray("0x8c109349c6bd91411d6bc962e080c4a3")),
                new Identifier("TokenSwapFarmBoost"), new Identifier("UserInfo"), typeParams);
        //DataPath dataPath = new DataPath.Resource(structTag);
        byte[] bcsBytes = structTag.bcsSerialize();//dataPath.bcsSerialize();
        StarcoinTreeHasher th = new StarcoinTreeHasher();
        Bytes pathHash = th.path(new Bytes(bcsBytes));
        System.out.println(HexUtils.byteArrayToHex(pathHash.getValue()));
        //313fcf74be39e19d75b6d028d28cf3e43efd92e95abd580971b6552667e69ee0
    }

    @Test
    void testValueHash() {
        //"{ACCOUNT_ADDRESS}/1/0x8c109349c6bd91411d6bc962e080c4a3::TokenSwapFarmBoost::UserInfo<0x00000000000000000000000000000001::STC::STC, 0x8c109349c6bd91411d6bc962e080c4a3::STAR::STAR>",
        //byte[] state = HexUtils.hexToByteArray("0xfa000000000000007b161ceeef010000000000000000000000000000000000000000000000000000");
        byte[] state = HexUtils.hexToByteArray("0x020001200f30a41872208c6324fa842889315b14f9be6f3dd0d5050686317adfdd0cda60");
        System.out.println(Arrays.toString(state));
        StarcoinTreeHasher th = new StarcoinTreeHasher();
        Bytes stateHash = th.valueHash(new Bytes(state));
        System.out.println(HexUtils.byteArrayToHex(stateHash.getValue()));
        //e5c11e706a534b191358b9954c2f03c371162d950ff81a7cd3d20701bbaec525
        //94cd17eeae8ffa6267ce9e56ec95088094800a0c219c2ffa3c132d6481ae80fe
    }

    @Test
    void testAccountStateBcsDeserialize() throws DeserializationError {
        byte[] bytes = HexUtils.hexToByteArray("0x020001200f30a41872208c6324fa842889315b14f9be6f3dd0d5050686317adfdd0cda60");
        AccountState accountState = AccountState.bcsDeserialize(bytes);
        System.out.println(accountState);
    }

    @Test
    public void testComputeRootByPathAndValueHash1() {
        String[] hexArray = new String[]{
                "0x1bab40b8314569919ffe0b23634e823f33e100db3998f98d7cfe7f08dba4edc8",
                "0x5350415253455f4d45524b4c455f504c414345484f4c4445525f484153480000",
                "0x508f52fd50dce8e8b8c6d616979420642c4c2a97a0b7ed8ec32c9275306cea62",
                "0xbfbadd336063931c0d647541f8f0907f5db2778a7a558dba4ddd091f0dcdf621",
                "0x68c12b8864da7f1c2e58aa7266fbc095033eb17fe79514656b07c2c6802f3f7c",
                "0xfea0285ae3676cb83361b6d220467f6dc7e2468f218d02cff8a8ef07babd071c",
                "0x8e124ef82820e17ef817ff1c0268f3fd556601f9320bdcccaeb870404dbe2372",
                "0x57247e09b018c1d5945f097c0ed1b6d41d5783de980fec9da1b9dc03878fe03b",
                "0xa23186f6e89c8e2ed959e9cc64dcb80281c44c74e26ac2300a8972f989580e46",
                "0x65bcf99787eeb8f64c33ec354f53b53da0162e8b0d544839031a719842fe7d77",
                "0xeef25137adc52a868e1d5e17e5931389b647fce5cfed3769dfd20e54acac8f8b",
                "0x01cdc788e064f90b6e49b3728bc8baaba85f4dc482c157dba859c681c533b58c",
                "0x3faf5ef565fa024338b258cbc6c0f4ee98223c0151f69c936a4a87fb51748193",
                "0x652b41a623995b15dd71bcc1793248fa4130d995e723996e9986d179e59b44cd",
                "0x4a2810f54ef2624679c69c88cf7f47719ee928218d54d1f5a3c339231677a538",
                "0xf65ccb1832976a7c7fad668ca44e4bb228a58e182a7c4437d17660adcc7dfc63"
        };
        byte[][] byteArrays = HexUtils.hexArrayToByteArrays(hexArray);
        System.out.println(byteArrays);
        SparseMerkleProof proof = new SparseMerkleProof(Bytes.toBytesArray(byteArrays), Bytes.EMPTY, Bytes.EMPTY);
        System.out.println(proof);

        StarcoinTreeHasher.Node leaf = new StarcoinTreeHasher.Node(
                HexUtils.hexToByteArray("0xa64823bd1efeac7ef50fb0fb5d5cb142823e4029d4da013d06e51b86deef73c8"),
                HexUtils.hexToByteArray("0x94cd17eeae8ffa6267ce9e56ec95088094800a0c219c2ffa3c132d6481ae80fe")
        );
        StarcoinTreeHasher th = new StarcoinTreeHasher();
        Bytes root = SparseMerkleProof.computeRootByPathAndValueHash(proof.getSideNodes(), new Bytes(leaf.getHash1()), new Bytes(leaf.getHash2()), th);
        System.out.println(HexUtils.byteArrayToHex(root.getValue()));

        Bytes expectedRoot = new Bytes(HexUtils.hexToByteArray("0x99163c0fc319b62c3897ada8f97881e396e33b30383f47e23d93aaed07d6806d"));
        Assertions.assertEquals(expectedRoot, root);
    }


    @Test
    public void testComputeRootByPathAndValueHash2() {
        String[] hexArray = new String[]{
                "0x24521d9cbd1bb73959b54a3993159b125f1500221e1455490189466858725948",
                "0xa5f028948c522a35e6a75775de25c097ffefee7d63c4949482e38df0428b3b6d",
                "0x33c4f5958cb1a1875eb1be51d2601e13f5e5a4f5518d578d4c6368ac0af6d648",
                "0xd9ff5eeb7dde4db48f44b79d54f7bb162b5a4ce32d583ee91431dea52d6fced1",
                "0xa2dbe6355af9d9f00d84d2e944b97841de2221451887e0fadbc957dbe39d1a3e",
                "0x3cc075bcc91302e92fb6a23880669085a0436a12e6e407aea6e7192344f41667",
                "0xfc8d88d2484e154836aca3afd927fec8a8168667d24ceaf5e4d3c22722020609"
        };
        byte[][] byteArrays = HexUtils.hexArrayToByteArrays(hexArray);
        System.out.println(byteArrays);
        SparseMerkleProof proof = new SparseMerkleProof(Bytes.toBytesArray(byteArrays), Bytes.EMPTY, Bytes.EMPTY);
        System.out.println(proof);

        StarcoinTreeHasher.Node leaf = new StarcoinTreeHasher.Node(
                HexUtils.hexToByteArray("0x313fcf74be39e19d75b6d028d28cf3e43efd92e95abd580971b6552667e69ee0"),
                HexUtils.hexToByteArray("0xe5c11e706a534b191358b9954c2f03c371162d950ff81a7cd3d20701bbaec525")
        );
        StarcoinTreeHasher th = new StarcoinTreeHasher();
        Bytes root = SparseMerkleProof.computeRootByPathAndValueHash(proof.getSideNodes(), new Bytes(leaf.getHash1()), new Bytes(leaf.getHash2()), th);
        System.out.println(HexUtils.byteArrayToHex(root.getValue()));

        Bytes expectedRoot = new Bytes(HexUtils.hexToByteArray("0x0f30a41872208c6324fa842889315b14f9be6f3dd0d5050686317adfdd0cda60"));
        Assertions.assertEquals(expectedRoot, root);
    }

}
