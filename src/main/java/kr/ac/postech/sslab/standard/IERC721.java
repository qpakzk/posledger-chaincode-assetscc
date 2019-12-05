package kr.ac.postech.sslab.standard;

import org.hyperledger.fabric.shim.Chaincode.Response;
import org.hyperledger.fabric.shim.ChaincodeStub;
import java.util.List;

public interface IERC721 {
    Response balanceOf(ChaincodeStub stub, List<String> args);
    Response ownerOf(ChaincodeStub stub, List<String> args);
    Response transferFrom(ChaincodeStub stub, List<String> args);
    Response approve(ChaincodeStub stub, List<String> args);
    Response setApprovalForAll(ChaincodeStub stub, List<String> args);
    Response getApproved(ChaincodeStub stub, List<String> args);
    Response isApprovedForAll(ChaincodeStub stub, List<String> args);
}