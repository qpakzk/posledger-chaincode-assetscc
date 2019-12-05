package kr.ac.postech.sslab.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ConcreteChaincodeBase extends ChaincodeBase {
    private HashMap<String, HashMap<String, Boolean>> operatorsApproval;

    public ConcreteChaincodeBase() {
        this.operatorsApproval = new HashMap<>();
    }

    protected HashMap<String, HashMap<String, Boolean>> getOperatorsApproval() { return this.operatorsApproval; }

    protected void setOperatorsApproval(ChaincodeStub stub, String owner, String operator, boolean approved) throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        String operatorsApproval = stub.getStringState("operatorsApproval");
        TypeReference type = new TypeReference<HashMap<String, HashMap<String, Boolean>>>() {};
        this.operatorsApproval = mapper.readValue(operatorsApproval, type);


        HashMap<String, Boolean> operatorMap;
        if (this.operatorsApproval.containsKey(owner)) {
            operatorMap = this.operatorsApproval.get(owner);
        }
        else {
            operatorMap = new HashMap<>();
        }

        operatorMap.put(operator, approved);
        this.operatorsApproval.put(owner, operatorMap);

        stub.putStringState("operatorsApproval", mapper.writeValueAsString(this.operatorsApproval));
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
                throw new Throwable("FAILURE");
            }

            List<String> args = stub.getParameters();
            if (args.size() != 0) {
                throw new Throwable("FAILURE");
            }

            ObjectMapper mapper = new ObjectMapper();

            String operatorsApproval = stub.getStringState("operatorsApproval");
            if (operatorsApproval.trim().length() == 0) {
                stub.putStringState("operatorsApproval", mapper.writeValueAsString(this.operatorsApproval));
            }

            return newSuccessResponse("SUCCESS");
        } catch (Throwable throwable) {
            return newErrorResponse("FAILURE");
        }
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        return newSuccessResponse("SUCCESS");
    }
}
