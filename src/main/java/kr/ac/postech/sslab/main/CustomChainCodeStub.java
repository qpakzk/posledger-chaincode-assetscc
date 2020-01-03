package kr.ac.postech.sslab.main;

import org.hyperledger.fabric.shim.ChaincodeStub;

public class CustomChainCodeStub {
    private static final ThreadLocal<ChaincodeStub> chaincodeStub = new ThreadLocal<>();

    public static void setChaincodeStub(ChaincodeStub stub) {
        chaincodeStub.set(stub);
    }

    public static ChaincodeStub getChaincodeStub() {
        return chaincodeStub.get();
    }
}
