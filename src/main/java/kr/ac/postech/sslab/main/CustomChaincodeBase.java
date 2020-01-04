package kr.ac.postech.sslab.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomChaincodeBase extends ChaincodeBase {
	private static final String ARG_MESSAGE = "Incorrect number of arguments, expecting %d";
	private static final String SUCCESS = "SUCCESS";

	private static ObjectMapper mapper = new ObjectMapper();
    protected static Map<String, Map<String, Boolean>> operatorsApproval = new HashMap<>();
    private static final String OPERATORS_APPROVAL = "OPERATORS_APPROVAL";

    protected static Map<String, Map<String, List<String>>> tokenTypes = new HashMap<>();
    private static final String TOKEN_TYPES = "TOKEN_TYPES";

    @Override
    public Response init(ChaincodeStub stub) {

        try {
            String func = stub.getFunction();

            if (!func.equals("init")) {
                return newErrorResponse("Function other than init is not supported");
            }

            List<String> args = stub.getParameters();
            if (!args.isEmpty()) {
                throw new IllegalArgumentException(String.format(ARG_MESSAGE, 0));
            }

            String operatorsApprovalString = stub.getStringState(OPERATORS_APPROVAL);
            if (operatorsApprovalString.trim().length() == 0) {
                stub.putStringState(OPERATORS_APPROVAL, mapper.writeValueAsString(operatorsApproval));
            }

            String tokenTypesString = stub.getStringState(TOKEN_TYPES);
            if (tokenTypesString.trim().length() == 0) {
                stub.putStringState(TOKEN_TYPES, mapper.writeValueAsString(tokenTypes));
            }

            return newSuccessResponse(SUCCESS);
        } catch (Exception e) {
            return newErrorResponse(e.getMessage());
        }
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        return newSuccessResponse();
    }
}
