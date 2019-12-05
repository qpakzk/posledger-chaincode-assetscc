package kr.ac.postech.sslab.main;

import kr.ac.postech.sslab.standard.BaseNFT;
import kr.ac.postech.sslab.standard.ERC721;
import kr.ac.postech.sslab.standard.IBaseNFT;
import kr.ac.postech.sslab.standard.IERC721;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.*;

public class Main extends ConcreteChaincodeBase implements IERC721, IBaseNFT {
    private ERC721 erc721 = new ERC721();
    private BaseNFT nft = new BaseNFT();

    @Override
    public Response init(ChaincodeStub stub) {
        return super.init(stub);
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        try {
            String func = stub.getFunction();
            List<String> args = stub.getParameters();

            switch (func) {
                case "balanceOf":
                    return this.balanceOf(stub, args);

                case "ownerOf":
                    return this.ownerOf(stub, args);

                case "transferFrom":
                    return this.transferFrom(stub, args);

                case "approve":
                    return this.approve(stub, args);

                case "setApprovalForAll":
                    return this.setApprovalForAll(stub, args);

                case "getApproved":
                    return this.getApproved(stub, args);

                case "isApprovedForAll":
                    return this.isApprovedForAll(stub, args);

                case "mint":
                    return this.mint(stub, args);

                case "burn":
                    return this.burn(stub, args);

                case "getType":
                    return this.getType(stub, args);

                case "setOwner":
                    return this.setOwner(stub, args);

                case "getOwner":
                    return this.getOwner(stub, args);

                case "setOperatorForCaller":
                    return this.setOperatorForCaller(stub, args);

                case "isOperatorForCaller":
                    return this.isOperatorForCaller(stub, args);

                case "setApprovee":
                    return this.setApprovee(stub, args);

                case "getApprovee":
                    return this.getApprovee(stub, args);

                default:
                    throw new Throwable("FAILURE");
            }

        } catch (Throwable throwable) {
            return newErrorResponse("FAILURE");
        }
    }

    @Override
    public Response balanceOf(ChaincodeStub stub, List<String> args) {
        return this.erc721.balanceOf(stub, args);
    }

    @Override
    public Response ownerOf(ChaincodeStub stub, List<String> args) {
       return this.erc721.ownerOf(stub, args);
    }

    @Override
    public Response transferFrom(ChaincodeStub stub, List<String> args) {
        return this.erc721.transferFrom(stub, args);
    }

    @Override
    public Response approve(ChaincodeStub stub, List<String> args) {
        return this.erc721.approve(stub, args);
    }

    @Override
    public Response setApprovalForAll(ChaincodeStub stub, List<String> args) {
        return this.erc721.setApprovalForAll(stub, args);
    }

    @Override
    public Response getApproved(ChaincodeStub stub, List<String> args) {
        return this.erc721.getApproved(stub, args);
    }

    @Override
    public Response isApprovedForAll(ChaincodeStub stub, List<String> args) {
        return this.erc721.isApprovedForAll(stub, args);
    }

    @Override
    public Response mint(ChaincodeStub stub, List<String> args) {
        if (args.size() == 2) {
            return this.erc721.mint(stub, args);
        }

        return this.nft.mint(stub, args);
    }

    @Override
    public Response burn(ChaincodeStub stub, List<String> args) {
        return this.nft.burn(stub, args);
    }

    @Override
    public Response getType(ChaincodeStub stub, List<String> args) {
       return this.nft.getType(stub, args);
    }

    @Override
    public Response setOwner(ChaincodeStub stub, List<String> args) {
        return this.nft.setOwner(stub, args);
    }

    @Override
    public Response getOwner(ChaincodeStub stub, List<String> args) {
        return this.nft.getOwner(stub, args);
    }

    @Override
    public Response setOperatorForCaller(ChaincodeStub stub, List<String> args) {
        return this.nft.setOperatorForCaller(stub, args);
    }

    @Override
    public Response isOperatorForCaller(ChaincodeStub stub, List<String> args) {
        return this.nft.isOperatorForCaller(stub, args);
    }

    @Override
    public Response setApprovee(ChaincodeStub stub, List<String> args) {
        return this.nft.setApprovee(stub, args);
    }

    @Override
    public Response getApprovee(ChaincodeStub stub, List<String> args) {
        return this.nft.getApprovee(stub, args);
    }

    public static void main(String[] args) {
        new Main().start(args);
    }

}
