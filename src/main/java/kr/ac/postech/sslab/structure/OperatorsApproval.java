package kr.ac.postech.sslab.structure;

import java.util.HashMap;
import java.util.Map;

public class OperatorsApproval {
    private OperatorsApproval() {}

    private static Map<String, Map<String, Boolean>> operators = new HashMap<>();

    public static Map<String, Map<String, Boolean>> getOperatorsApproval() {
        return operators;
    }

    public static void setOperatorsApproval(Map<String, Map<String, Boolean>> operators) {
        OperatorsApproval.operators = operators;
    }
}
