package kr.ac.postech.sslab.extension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.postech.sslab.main.CustomChaincodeBase;
import kr.ac.postech.sslab.structure.TokenTypes;
import kr.ac.postech.sslab.util.DataTypeConversion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

import static kr.ac.postech.sslab.constant.Key.TOKEN_TYPES;

public class XType extends CustomChaincodeBase {
    private static final Log LOG = LogFactory.getLog(XType.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    public static boolean registerTokenType(ChaincodeStub stub, String type, String json) throws IOException {
        Map<String, List<String>> attributes
                = mapper.readValue(json, new TypeReference<HashMap<String, List<String>>>(){});

        TokenTypes.getTokenTypes().put(type, attributes);

        stub.putStringState(TOKEN_TYPES, toJSONString(TokenTypes.getTokenTypes()));
        return true;
    }

    public static List<String> tokenTypesOf() {
        return new ArrayList<>(TokenTypes.getTokenTypes().keySet());
    }

    public static Map<String, List<String>> getTokenType(String type) {
        return TokenTypes.getTokenTypes().get(type);
    }

    public static boolean initXAttr(String type, Map<String, Object> xattr) {
        if (!TokenTypes.getTokenTypes().containsKey(type)) {
            LOG.info(String.format("XType::initXAttr:: No Token type %s in tokenTypes", type));
            return false;
        }

        Map<String, List<String>> attributes = TokenTypes.getTokenTypes().get(type);
        if (xattr != null) {
            for (String key : xattr.keySet()) {
                if (!attributes.containsKey(key)) {
                    LOG.info(String.format("XType::initXAttr:: No attribute %s in xattr", key));
                    return false;
                }
            }

            for (Map.Entry<String, List<String>> entry : attributes.entrySet()) {
                if (!xattr.containsKey(entry.getKey())) {
                    List<String> attr = entry.getValue();
                    if (entry.getValue().size() != 2) {
                        LOG.info("XType::initXAttr:: List attr should have two elements");
                        return false;
                    }

                    Object value = DataTypeConversion.strToDataType(entry.getValue().get(0), entry.getValue().get(1));
                    if (value == null) {
                        return false;
                    }

                    xattr.put(entry.getKey(), value);
                }
            }
        }
        return true;
    }

    public static boolean addXAttrForEERC721(Map<String, Object> xattr) {
        final String PARENT_KEY = "parent";
        final String CHILDREN_KEY = "children";
        final String ACTIVATED_KEY = "activated";

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
        if (uri != null && (uri.keySet().size() != 2
                || !uri.containsKey("path") || !uri.containsKey("hash"))) {
            return false;
        }

        return true;
    }

    private static String toJSONString(Map<String, Map<String, List<String>>> map) throws JsonProcessingException {
        return mapper.writeValueAsString(map);
    }
}
