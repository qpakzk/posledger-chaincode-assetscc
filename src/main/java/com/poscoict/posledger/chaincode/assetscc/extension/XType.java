package com.poscoict.posledger.chaincode.assetscc.extension;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poscoict.posledger.chaincode.assetscc.structure.TokenTypeManager;
import com.poscoict.posledger.chaincode.assetscc.main.CustomChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.io.IOException;
import java.util.*;

import static com.poscoict.posledger.chaincode.assetscc.constant.DataType.*;
import static com.poscoict.posledger.chaincode.assetscc.constant.Key.*;

public class XType extends CustomChaincodeBase {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static boolean enroll(ChaincodeStub stub, String admin, String type, String json) throws IOException {
        Map<String, List<String>> attributes = objectMapper.readValue(json, new TypeReference<HashMap<String, List<String>>>() {});

        if (!attributes.containsKey(ADMIN_KEY)) {
            List<String> adminPair = new ArrayList<>(Arrays.asList(STRING, admin));
            attributes.put(ADMIN_KEY, adminPair);
        }

        if (!attributes.containsKey(PARENT_KEY)) {
            String parentValue = "";
            List<String> parent = new ArrayList<>(Arrays.asList(STRING, parentValue));
            attributes.put(PARENT_KEY, parent);
        }

        if (!attributes.containsKey(CHILDREN_KEY)) {
            List<String> childrenValue = new ArrayList<>();
            List<String> children = new ArrayList<>(Arrays.asList(LIST_STRING, childrenValue.toString()));
            attributes.put(CHILDREN_KEY, children);
        }

        if (!attributes.containsKey(ACTIVATED_KEY)) {
            boolean activatedValue = true;
            List<String> activated = new ArrayList<>(Arrays.asList(BOOLEAN, Boolean.toString(activatedValue)));
            attributes.put(ACTIVATED_KEY, activated);
        }

        TokenTypeManager manager = TokenTypeManager.read(stub);
        return manager.addTokenType(stub, type, attributes);
    }

    public static boolean drop(ChaincodeStub stub, String admin, String tokenType) throws IOException {
        TokenTypeManager manager = TokenTypeManager.read(stub);

        if (!admin.equals(manager.getAdmin(tokenType))) {
            return false;
        }

        return manager.removeTokenType(stub, tokenType);
    }

    public static List<String> tokenTypesOf(ChaincodeStub stub) throws IOException {
        TokenTypeManager manager = TokenTypeManager.read(stub);
        return new ArrayList<>(manager.getTokenTypes().keySet());
    }

    public static boolean update(ChaincodeStub stub, String admin, String tokenType, Map<String, List<String>> attributes) throws IOException {
        TokenTypeManager manager = TokenTypeManager.read(stub);

        if (!admin.equals(manager.getAdmin(tokenType))) {
            return false;
        }

        if (!attributes.containsKey(ADMIN_KEY)) {
            List<String> adminPair = new ArrayList<>(Arrays.asList(STRING, admin));
            attributes.put(ADMIN_KEY, adminPair);
        }

        if (!attributes.containsKey(PARENT_KEY)) {
            String parentValue = "";
            List<String> parent = new ArrayList<>(Arrays.asList(STRING, parentValue));
            attributes.put(PARENT_KEY, parent);
        }

        if (!attributes.containsKey(CHILDREN_KEY)) {
            List<String> childrenValue = new ArrayList<>();
            List<String> children = new ArrayList<>(Arrays.asList(LIST_STRING, childrenValue.toString()));
            attributes.put(CHILDREN_KEY, children);
        }

        if (!attributes.containsKey(ACTIVATED_KEY)) {
            boolean activatedValue = true;
            List<String> activated = new ArrayList<>(Arrays.asList(BOOLEAN, Boolean.toString(activatedValue)));
            attributes.put(ACTIVATED_KEY, activated);
        }

        return manager.setTokenType(stub, tokenType, attributes);
    }

    public static Map<String, List<String>> retrieve(ChaincodeStub stub, String tokenType) throws IOException {
        TokenTypeManager manager = TokenTypeManager.read(stub);
        return manager.getTokenType(tokenType);
    }

    public static boolean enrollAttribute(ChaincodeStub stub, String admin, String tokenType, String attribute, String dataType, String initialValue) throws IOException {
        TokenTypeManager manager = TokenTypeManager.read(stub);

        if (!admin.equals(manager.getAdmin(tokenType))) {
            return false;
        }

        return manager.addAttributeOfTokenType(stub, tokenType, attribute, dataType, initialValue);
    }

    public static boolean dropAttribute(ChaincodeStub stub, String admin, String tokenType, String attribute) throws IOException {
        TokenTypeManager manager = TokenTypeManager.read(stub);

        if (!admin.equals(manager.getAdmin(tokenType))) {
            return false;
        }

        return manager.removeAttributeOfTokenType(stub, tokenType, attribute);
    }

    public static boolean updateAttribute(ChaincodeStub stub, String admin, String tokenType, String attribute, List<String> pair) throws IOException {
        TokenTypeManager manager = TokenTypeManager.read(stub);

        if (!admin.equals(manager.getAdmin(tokenType))) {
            return false;
        }

        return manager.setAttributeOfTokenType(stub, tokenType, attribute, pair);
    }

    public static List<String> retrieveAttribute(ChaincodeStub stub, String tokenType, String attribute) throws IOException {
        TokenTypeManager manager = TokenTypeManager.read(stub);
        return manager.getAttributeOfTokenType(tokenType, attribute);
    }
}
