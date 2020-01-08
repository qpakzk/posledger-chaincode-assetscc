package com.poscoict.posledger.chaincode.assetscc.main;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poscoict.posledger.chaincode.assetscc.constant.Key;
import com.poscoict.posledger.chaincode.assetscc.constant.Message;
import com.poscoict.posledger.chaincode.assetscc.structure.OperatorsApproval;
import com.poscoict.posledger.chaincode.assetscc.structure.TokenTypeManager;
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

            OperatorsApproval approval = OperatorsApproval.read(stub);
            Map<String, Map<String, Boolean>> operators = approval.getOperatorsApproval();
            approval.setOperatorsApproval(stub, operators);

            TokenTypeManager manager = TokenTypeManager.read(stub);
            Map<String, Map<String, List<String>>> tokenTypes = manager.getTokenTypes();
            manager.setTokenTypes(stub, tokenTypes);

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
