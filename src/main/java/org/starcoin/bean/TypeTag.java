//package org.starcoin.bean;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//
//public class TypeTag {
//    /*
//          "type_tag": {
//          "Struct": {
//            "address": "0x00000000000000000000000000000001",
//            "module": "Account",
//            "name": "DepositEvent",
//            "type_params": []
//          }
//        },
//     */
//
//    @JsonProperty("Struct")
//    StructTag struct;
//
//    public StructTag getStruct() {
//        return struct;
//    }
//
//    public void setStruct(StructTag struct) {
//        this.struct = struct;
//    }
//
//    @Override
//    public String toString() {
//        return "TypeTag{" +
//                "struct=" + struct +
//                '}';
//    }
//}
