package kr.ac.postech.sslab.structure;

import java.util.HashMap;
import java.util.Map;

public class OperatorsApproval {
    private static Map<String, Map<String, Boolean>> operatorsApproval = new HashMap<>();

    public static Map<String, Map<String, Boolean>> getOperatorsApproval() {
        return operatorsApproval;
    }

    public static void setOperatorsApproval(Map<String, Map<String, Boolean>> operatorsApproval) {
        OperatorsApproval.operatorsApproval = operatorsApproval;
    }
}
