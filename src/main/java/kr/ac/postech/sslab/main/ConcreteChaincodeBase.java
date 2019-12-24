package kr.ac.postech.sslab.main;

import kr.ac.postech.sslab.exception.NoMatchException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ConcreteChaincodeBase extends ChaincodeBase {
    private HashMap<String, HashMap<String, Boolean>> operatorsApproval;
	private static final String ARG_MESSAGE = "Incorrect number of arguments, expecting %d";
	private static final String SUCCESS = "SUCCESS";
	private static final String KEY = "operatorsApproval";

    public ConcreteChaincodeBase() {
        this.operatorsApproval = new HashMap<>();
    }

    protected HashMap<String, HashMap<String, Boolean>> getOperatorsApproval() { return this.operatorsApproval; }

    protected void setOperatorsApproval(ChaincodeStub stub, String owner, String operator, boolean approved) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        String operatorsApprovalString = stub.getStringState(KEY);
        TypeReference type = new TypeReference<HashMap<String, HashMap<String, Boolean>>>() {};
        this.operatorsApproval = mapper.readValue(operatorsApprovalString, type);


        HashMap<String, Boolean> operatorMap;
        if (this.operatorsApproval.containsKey(owner)) {
            operatorMap = this.operatorsApproval.get(owner);
        }
        else {
            operatorMap = new HashMap<>();
        }

        operatorMap.put(operator, approved);
        this.operatorsApproval.put(owner, operatorMap);

        stub.putStringState(KEY, mapper.writeValueAsString(this.operatorsApproval));
    }

    protected boolean isOperatorForOwner(String owner, String operator) {
        if (this.operatorsApproval.containsKey(owner)) {
            return this.operatorsApproval.get(owner).getOrDefault(operator, false);
        }
        else {
            return false;
        }
    }

    @Override
    public Response init(ChaincodeStub stub) {

        try {
            String func = stub.getFunction();

            if (!func.equals("init")) {
                throw new NoMatchException("'init' function is only allowed");
            }

            List<String> args = stub.getParameters();
            if (!args.isEmpty()) {
                throw new IllegalArgumentException(String.format(ARG_MESSAGE, 0));
            }

            ObjectMapper mapper = new ObjectMapper();

            String operatorsApprovalString = stub.getStringState(KEY);
            if (operatorsApprovalString.trim().length() == 0) {
                stub.putStringState(KEY, mapper.writeValueAsString(this.operatorsApproval));
            }

            return newSuccessResponse(SUCCESS);
        } catch (Exception e) {
            return newErrorResponse(e.getMessage());
        }
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        return newSuccessResponse(SUCCESS);
    }
}
