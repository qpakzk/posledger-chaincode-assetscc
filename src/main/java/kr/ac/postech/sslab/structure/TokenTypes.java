package kr.ac.postech.sslab.structure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenTypes {
    private TokenTypes() {}

    private static Map<String, Map<String, List<String>>> types = new HashMap<>();

    public static Map<String, Map<String, List<String>>> getTokenTypes() {
        return types;
    }

    public static void setTokenTypes(Map<String, Map<String, List<String>>> types) {
        TokenTypes.types = types;
    }
}
