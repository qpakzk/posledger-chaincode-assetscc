package com.poscoict.posledger.chaincode.assetscc.structure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenTypeManager {
    private TokenTypeManager() {}

    //Map<token type, Map<attribute, [data type, initial value]>>
    private static Map<String, Map<String, List<String>>> tokenTypes = new HashMap<>();

    public static Map<String, Map<String, List<String>>> getTokenTypes() {
        return tokenTypes;
    }

    public static void setTokenTypes(Map<String, Map<String, List<String>>> tokenTypes) {
        TokenTypeManager.tokenTypes = tokenTypes;
    }
}
