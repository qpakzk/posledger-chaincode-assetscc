package kr.ac.postech.sslab.extension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.postech.sslab.main.CustomChaincodeBase;
import kr.ac.postech.sslab.main.CustomMain;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

public class XType extends CustomChaincodeBase {
    private static final Log LOG = LogFactory.getLog(CustomMain.class);

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String TOKEN_TYPES = "TOKEN_TYPES";

    public static boolean registerTokenType(ChaincodeStub stub, String type, String json) throws IOException {
        Map<String, List<String>> attributes
                = mapper.readValue(json, new TypeReference<HashMap<String, List<String>>>(){});

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
            LOG.info("XType::initXAttr:: No Token type in tokenTypes");
            return false;
        }

        Map<String, List<String>> attributes = tokenTypes.get(type);
        if (xattr != null) {
            for (String key : xattr.keySet()) {
                if (!attributes.containsKey(key)) {
                    LOG.info("XType::initXAttr:: No attributes in xattr");
                    return false;
                }
            }

            for (String key : attributes.keySet()) {
                if (!xattr.containsKey(key)) {
                    List<String> attr = attributes.get(key);
                    if (attr.size() != 2) {
                        LOG.info("XType::initXAttr:: List attr should have two elements");
                        return false;
                    }

                    switch (attr.get(0)) {
                        case INTEGER: {
                            int value = Integer.parseInt(attr.get(1));
                            xattr.put(key, value);
                            break;
                        }

                        case BIG_INTEGER: {
                            BigInteger value = new BigInteger(attr.get(1));
                            xattr.put(key, value);
                            break;
                        }

                        case DOUBLE: {
                            double value = Double.parseDouble(attr.get(1));
                            xattr.put(key, value);
                            break;
                        }

                        case BYTE: {
                            byte value = Byte.parseByte(attr.get(1));
                            xattr.put(key, value);
                            break;
                        }

                        case STRING: {
                            String value = attr.get(1);
                            xattr.put(key, value);
                            break;
                        }

                        case BOOLEAN: {
                            boolean value = Boolean.parseBoolean(attr.get(1));
                            xattr.put(key, value);
                            break;
                        }

                        case LIST_INTEGER: {
                            List<String> values1 = toList(attr.get(1));
                            List<Integer> values2 = new ArrayList<>();
                            for (String value1 : values1) {
                                int value2 = Integer.parseInt(value1);
                                values2.add(value2);
                            }

                            xattr.put(key, values2);
                            break;
                        }

                        case LIST_BIG_INTEGER: {
                            List<String> values1 = toList(attr.get(1));
                            List<BigInteger> values2 = new ArrayList<>();
                            for (String value1 : values1) {
                                BigInteger value2 = new BigInteger(value1);
                                values2.add(value2);
                            }

                            xattr.put(key, values2);
                            break;
                        }

                        case LIST_DOUBLE: {
                            List<String> values1 = toList(attr.get(1));
                            List<Double> values2 = new ArrayList<>();
                            for (String value1 : values1) {
                                double value2 = Double.parseDouble(value1);
                                values2.add(value2);
                            }

                            xattr.put(key, values2);
                            break;
                        }

                        case LIST_BYTE: {
                            List<String> values1 = toList(attr.get(1));
                            List<Byte> values2 = new ArrayList<>();
                            for (String value1 : values1) {
                                byte value2 = Byte.parseByte(value1);
                                values2.add(value2);
                            }

                            xattr.put(key, values2);
                            break;
                        }

                        case LIST_STRING: {
                            List<String> values = toList(attr.get(1));
                            xattr.put(key, values);
                            break;
                        }

                        case LIST_BOOLEAN: {
                            List<String> values1 = toList(attr.get(1));
                            List<Boolean> values2 = new ArrayList<>();
                            for (String value1 : values1) {
                                boolean value2 = Boolean.parseBoolean(value1);
                                values2.add(value2);
                            }

                            xattr.put(key, values2);
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

    public static Map<String, List<String>> getTokenType(String type) {
        return tokenTypes.get(type);
    }

    public static boolean checkURI(Map<String, String> uri) {
        if (uri != null && (uri.keySet().size() != 2
                || !uri.containsKey("path") || !uri.containsKey("hash"))) {
            return false;
        }

        return true;
    }

    private static String toJSONString(Map<String, Map<String, List<String>>> map) throws JsonProcessingException {
        return mapper.writeValueAsString(map);
    }

    private static List<String> toList(String value) {
        return Arrays.asList(value.substring(1, value.length() - 1).split(", "));
    }
}
