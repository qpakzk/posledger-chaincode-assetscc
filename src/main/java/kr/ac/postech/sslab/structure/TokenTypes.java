package kr.ac.postech.sslab.structure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenTypes {
    private static Map<String, Map<String, List<String>>> tokenTypes = new HashMap<>();

    public static Map<String, Map<String, List<String>>> getTokenTypes() {
        return tokenTypes;
    }

    public static void setTokenTypes(Map<String, Map<String, List<String>>> tokenTypes) {
        TokenTypes.tokenTypes = tokenTypes;
    }
}
