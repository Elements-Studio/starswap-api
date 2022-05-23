package org.starcoin.utils;

import org.starcoin.types.AccountAddress;
import org.starcoin.types.Identifier;
import org.starcoin.types.StructTag;
import org.starcoin.types.TypeTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MiscUtils {
    private static final String COLON_COLON = "::";
    private static final String LT = "<";
    private static final String GT = ">";
    private static final String COMMA = ",";

    private MiscUtils() {
    }


    public static StructTag parseStructTag(String s) {
        String[] parts = s.split(COLON_COLON, 3);
        if (parts.length != 3) {
            throw new IllegalArgumentException(s);
        }
        String address = parts[0];
        String moduleName = parts[1];
        String structName;
        List<TypeTag> typeParams;
        if (!parts[2].contains(LT)) {
            structName = parts[2];
            typeParams = Collections.emptyList();
        } else {
            int idx_lt = parts[2].indexOf(LT);
            int idx_gt = parts[2].lastIndexOf(GT);
            if (idx_lt < 1 || idx_gt < 0 || idx_lt > idx_gt) {
                throw new IllegalArgumentException(s);
            }
            structName = parts[2].substring(0, idx_lt);
            String ts = parts[2].substring(idx_lt + 1, idx_gt);
            typeParams = new ArrayList<>();
            for (String t : ts.split(COMMA)) {
                if (!t.contains(COLON_COLON)) {
                    throw new UnsupportedOperationException("ONLY support struct as type param");
                }
                typeParams.add(new TypeTag.Struct(parseStructTag(t.trim())));
            }
        }
        return new StructTag(AccountAddress.valueOf(HexUtils.hexToByteArray(address)),
                new Identifier(moduleName), new Identifier(structName), typeParams);
    }
}
