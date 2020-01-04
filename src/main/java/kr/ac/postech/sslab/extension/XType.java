package kr.ac.postech.sslab.extension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.util.Pair;
import kr.ac.postech.sslab.main.CustomChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XType extends CustomChaincodeBase {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String TOKEN_TYPES = "TOKEN_TYPES";

    public static boolean registerTokenType(ChaincodeStub stub, String type, String json) throws IOException {
        Map<String, Pair<String, Object>> attributes
                = mapper.readValue(json, new TypeReference<HashMap<String, Pair<String, Object>>>(){});

        tokenTypes.put(type, attributes);

        stub.putStringState(TOKEN_TYPES, toJSONString(tokenTypes));
        return true;
    }

    public static boolean initXAttr(String type, Map<String, Object> xattr) {
        final String INTEGER = "Integer";
        final String BIG_INTEGER = "BigInteger";
        final String DOUBLE = "Double";
        final String BYTE = "Byte";
        final String STRING = "String";
        final String BOOLEAN = "Boolean";
        final String LIST_INTEGER = "[Integer]";
        final String LIST_BIG_INTEGER = "[BigInteger]";
        final String LIST_DOUBLE = "[Double]";
        final String LIST_BYTE = "[Byte]";
        final String LIST_STRING = "[String]";
        final String LIST_BOOLEAN = "[Boolean]";

        if (!tokenTypes.containsKey(type)) {
            return false;
        }

        Map<String, Pair<String, Object>> attributes = tokenTypes.get(type);
        if (xattr != null) {
            for (String key : xattr.keySet()) {
                if (!attributes.containsKey(key)) {
                    return false;
                }
            }

            for (String key : attributes.keySet()) {
                if (!xattr.containsKey(key)) {
                    Pair<String, Object> attr = attributes.get(key);
                    switch (attr.getKey()) {
                        case INTEGER: {
                            int value = (int) attr.getValue();
                            xattr.put(key, value);
                            break;
                        }

                        case BIG_INTEGER: {
                            BigInteger value = (BigInteger) attr.getValue();
                            xattr.put(key, value);
                            break;
                        }

                        case DOUBLE: {
                            double value = (double) attr.getValue();
                            xattr.put(key, value);
                            break;
                        }

                        case BYTE: {
                            byte value = (byte) attr.getValue();
                            xattr.put(key, value);
                            break;
                        }

                        case STRING: {
                            String value = (String) attr.getValue();
                            xattr.put(key, value);
                            break;
                        }

                        case BOOLEAN: {
                            boolean value = (boolean) attr.getValue();
                            xattr.put(key, value);
                            break;
                        }

                        case LIST_INTEGER: {
                            List<Integer> value = (ArrayList<Integer>) attr.getValue();
                            xattr.put(key, value);
                            break;
                        }

                        case LIST_BIG_INTEGER: {
                            List<BigInteger> value = (ArrayList<BigInteger>) attr.getValue();
                            xattr.put(key, value);
                            break;
                        }

                        case LIST_DOUBLE: {
                            List<Double> value = (ArrayList<Double>) attr.getValue();
                            xattr.put(key, value);
                            break;
                        }

                        case LIST_BYTE: {
                            List<Byte> value = (ArrayList<Byte>) attr.getValue();
                            xattr.put(key, value);
                            break;
                        }

                        case LIST_STRING: {
                            List<String> value = (List<String>) attr.getValue();
                            xattr.put(key, value);
                            break;
                        }

                        case LIST_BOOLEAN: {
                            List<Boolean> value = (List<Boolean>) attr.getValue();
                            xattr.put(key, value);
                            break;
                        }
                    }
                }
            }
        }
        return true;
    }

    public static List<String> tokenTypesOf() {
        return new ArrayList<>(tokenTypes.keySet());
    }

    public static Map<String, Pair<String, Object>> getTokenType(String type) {
        return tokenTypes.get(type);
    }

    public static boolean checkURI(Map<String, String> uri) {
        if (uri != null && (uri.keySet().size() != 2
                || !uri.containsKey("path") || !uri.containsKey("hash"))) {
            return false;
        }

        return true;
    }


    private static String toJSONString(Map<String, Map<String, Pair<String, Object>>> map) throws JsonProcessingException {
        return mapper.writeValueAsString(map);
    }
}
