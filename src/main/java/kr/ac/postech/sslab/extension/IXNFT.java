package kr.ac.postech.sslab.extension;

import org.hyperledger.fabric.shim.Chaincode.Response;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.List;

public interface IXNFT {
    Response setURI(ChaincodeStub stub, List<String> args);
    Response getURI(ChaincodeStub stub, List<String> args);
    Response setXAttr(ChaincodeStub stub, List<String> args);
    Response getXAttr(ChaincodeStub stub, List<String> args);
}
