package kr.ac.postech.sslab.standard;

import org.hyperledger.fabric.shim.Chaincode.Response;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.List;

public interface IBaseNFT {
    Response mint(ChaincodeStub stub, List<String> args);
    Response burn(ChaincodeStub stub, List<String> args);
    Response getType(ChaincodeStub stub, List<String> args);
    Response setOwner(ChaincodeStub stub, List<String> args);
    Response getOwner(ChaincodeStub stub, List<String> args);
    Response setOperatorForCaller(ChaincodeStub stub, List<String> args);
    Response isOperatorForCaller(ChaincodeStub stub, List<String> args);
    Response setApprovee(ChaincodeStub stub, List<String> args);
    Response getApprovee(ChaincodeStub stub, List<String> args);
}