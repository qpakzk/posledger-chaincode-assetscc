package com.poscoict.posledger.chaincode.assetscc.main;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poscoict.posledger.chaincode.assetscc.constant.Key;
import com.poscoict.posledger.chaincode.assetscc.constant.Message;
import com.poscoict.posledger.chaincode.assetscc.structure.OperatorsApproval;
import com.poscoict.posledger.chaincode.assetscc.structure.TokenTypes;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomChaincodeBase extends ChaincodeBase {
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Response init(ChaincodeStub stub) {

        try {
            String func = stub.getFunction();

            if (!func.equals("init")) {
                return newErrorResponse("Function other than init is not supported");
            }

            List<String> args = stub.getParameters();
            if (!args.isEmpty()) {
                throw new IllegalArgumentException(String.format(Message.ARG_MESSAGE, "0"));
            }

            String operatorsApprovalString = stub.getStringState(Key.OPERATORS_APPROVAL);
            if (operatorsApprovalString.trim().length() == 0) {
                stub.putStringState(Key.OPERATORS_APPROVAL, objectMapper.writeValueAsString(OperatorsApproval.getOperatorsApproval()));
            }
            else {
                OperatorsApproval.setOperatorsApproval(objectMapper.readValue(operatorsApprovalString,
                        new TypeReference<HashMap<String, Map<String, Boolean>>>(){}));
            }

            String tokenTypesString = stub.getStringState(Key.TOKEN_TYPES);
            if (tokenTypesString.trim().length() == 0) {
                stub.putStringState(Key.TOKEN_TYPES, objectMapper.writeValueAsString(TokenTypes.getTokenTypes()));
            }
            else {
                TokenTypes.setTokenTypes(objectMapper.readValue(tokenTypesString,
                        new TypeReference<HashMap<String, Map<String, List<String>>>>() {}));
            }

            return newSuccessResponse();
        } catch (Exception e) {
            return newErrorResponse(e.getMessage());
        }
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        return newSuccessResponse();
    }
}
