package com.poscoict.posledger.chaincode.assetscc.extension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poscoict.posledger.chaincode.assetscc.structure.TokenTypeManager;
import com.poscoict.posledger.chaincode.assetscc.util.DataTypeConversion;
import com.poscoict.posledger.chaincode.assetscc.main.CustomChaincodeBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

import static com.poscoict.posledger.chaincode.assetscc.constant.Key.*;
import static com.poscoict.posledger.chaincode.assetscc.constant.Message.NO_ATTRIBUTE_MESSAGE;
import static com.poscoict.posledger.chaincode.assetscc.constant.Message.NO_TOKEN_TYPE_MESSAGE;

public class XType extends CustomChaincodeBase {
    private static final Log LOG = LogFactory.getLog(XType.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static boolean registerTokenType(ChaincodeStub stub, String type, String json) throws IOException {
        Map<String, List<String>> attributes
                = objectMapper.readValue(json, new TypeReference<HashMap<String, List<String>>>(){});

        TokenTypeManager.getTokenTypes().put(type, attributes);

        stub.putStringState(TOKEN_TYPES, toJSONString(TokenTypeManager.getTokenTypes()));
        return true;
    }

    public static List<String> tokenTypesOf() {
        return new ArrayList<>(TokenTypeManager.getTokenTypes().keySet());
    }

    public static Map<String, List<String>> getTokenType(String type) {
        return TokenTypeManager.getTokenTypes().get(type);
    }

    public static boolean initXAttr(String type, Map<String, Object> xattr) {
        if (!TokenTypeManager.getTokenTypes().containsKey(type)) {
            LOG.error(NO_TOKEN_TYPE_MESSAGE);
            return false;
        }

        Map<String, List<String>> attributes = TokenTypeManager.getTokenTypes().get(type);
        if (xattr != null) {
            if (!existKeys(xattr, attributes)) {
                return false;
            }

            for (Map.Entry<String, List<String>> entry : attributes.entrySet()) {
                if(!insertNewEntries(entry.getKey(), entry.getValue(), xattr)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean existKeys(Map<String, Object> xattr, Map<String, List<String>> attributes) {
        for (String key : xattr.keySet()) {
            if (!attributes.containsKey(key)) {
                LOG.error(NO_ATTRIBUTE_MESSAGE);
                return false;
            }
        }

        return true;
    }

    private static boolean insertNewEntries(String key, List<String> value, Map<String, Object> xattr) {
        if (!xattr.containsKey(key)) {
            if (value.size() != 2) {
                return false;
            }

            Object object = DataTypeConversion.strToDataType(value.get(0), value.get(1));
            if (object == null) {
                return false;
            }

            xattr.put(key, object);
        }

        return true;
    }

    public static boolean addXAttrForEERC721(Map<String, Object> xattr) {
        if (!xattr.containsKey(PARENT_KEY)) {
            xattr.put(PARENT_KEY, BigInteger.valueOf(-1));
        }

        if (!xattr.containsKey(CHILDREN_KEY)) {
            xattr.put(CHILDREN_KEY, new ArrayList<String>());
        }

        if (!xattr.containsKey(ACTIVATED_KEY)) {
            xattr.put(ACTIVATED_KEY, true);
        }

        return true;
    }

    public static boolean checkURI(Map<String, String> uri) {
        return uri == null || (uri.keySet().size() == 2
                && uri.containsKey("path") && uri.containsKey("hash"));
    }

    private static String toJSONString(Map<String, Map<String, List<String>>> map) throws JsonProcessingException {
        return objectMapper.writeValueAsString(map);
    }
}
