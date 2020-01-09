package com.poscoict.posledger.chaincode.assetscc.main;

import com.poscoict.posledger.chaincode.assetscc.constant.Message;
import com.poscoict.posledger.chaincode.assetscc.structure.OperatorsApproval;
import com.poscoict.posledger.chaincode.assetscc.structure.TokenTypeManager;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import java.util.List;
import java.util.Map;

import static com.poscoict.posledger.chaincode.assetscc.constant.Function.INIT_FUNCTION_NAME;
import static com.poscoict.posledger.chaincode.assetscc.constant.Message.INIT_FUNCTION_MESSAGE;

public class CustomChaincodeBase extends ChaincodeBase {
    @Override
    public Response init(ChaincodeStub stub) {

        try {
            String func = stub.getFunction();

            if (!func.equals(INIT_FUNCTION_NAME)) {
                return newErrorResponse(INIT_FUNCTION_MESSAGE);
            }

            List<String> args = stub.getParameters();
            if (!args.isEmpty()) {
                throw new IllegalArgumentException(String.format(Message.ARG_MESSAGE, "0"));
            }

            OperatorsApproval approval = OperatorsApproval.read(stub);
            Map<String, Map<String, Boolean>> operators = approval.getOperatorsApproval();
            if (operators.isEmpty()) {
                approval.setOperatorsApproval(stub, operators);
            }

            TokenTypeManager manager = TokenTypeManager.read(stub);
            Map<String, Map<String, List<String>>> tokenTypes = manager.getTokenTypes();
            if (tokenTypes.isEmpty()) {
                manager.setTokenTypes(stub, tokenTypes);
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
