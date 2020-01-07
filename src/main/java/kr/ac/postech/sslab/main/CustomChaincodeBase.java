package kr.ac.postech.sslab.main;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.postech.sslab.structure.OperatorsApproval;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kr.ac.postech.sslab.constant.Key.OPERATORS_APPROVAL;
import static kr.ac.postech.sslab.constant.Key.TOKEN_TYPES;
import static kr.ac.postech.sslab.constant.Message.ARG_MESSAGE;

public class CustomChaincodeBase extends ChaincodeBase {
    private static final Log LOG = LogFactory.getLog(CustomChaincodeBase.class);

	private static ObjectMapper mapper = new ObjectMapper();

    protected static Map<String, Map<String, List<String>>> tokenTypes = new HashMap<>();

    @Override
    public Response init(ChaincodeStub stub) {

        try {
            String func = stub.getFunction();

            if (!func.equals("init")) {
                return newErrorResponse("Function other than init is not supported");
            }

            List<String> args = stub.getParameters();
            if (!args.isEmpty()) {
                throw new IllegalArgumentException(String.format(ARG_MESSAGE, "0"));
            }

            String operatorsApprovalString = stub.getStringState(OPERATORS_APPROVAL);
            if (operatorsApprovalString.trim().length() == 0) {
                stub.putStringState(OPERATORS_APPROVAL, mapper.writeValueAsString(OperatorsApproval.getOperatorsApproval()));
            }
            else {
                LOG.info("CustomChaincodeBase::init [operatorsApproval] " + operatorsApprovalString);
                OperatorsApproval.setOperatorsApproval(mapper.readValue(operatorsApprovalString,
                        new TypeReference<HashMap<String, Map<String, Boolean>>>(){}));
            }

            String tokenTypesString = stub.getStringState(TOKEN_TYPES);
            if (tokenTypesString.trim().length() == 0) {
                stub.putStringState(TOKEN_TYPES, mapper.writeValueAsString(tokenTypes));
            }
            else {
                LOG.info("CustomChaincodeBase::init [tokenTypes] " + tokenTypesString);
                tokenTypes = mapper.readValue(tokenTypesString, new TypeReference<HashMap<String, Map<String, List<String>>>>() {});
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
