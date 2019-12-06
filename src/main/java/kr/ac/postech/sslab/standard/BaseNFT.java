package kr.ac.postech.sslab.standard;

import kr.ac.postech.sslab.main.ConcreteChaincodeBase;
import kr.ac.postech.sslab.nft.NFT;
import kr.ac.postech.sslab.user.Address;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ResponseUtils;
import java.util.List;

public class BaseNFT extends ConcreteChaincodeBase implements IBaseNFT {
	private static final String ARG_MESSAGE = "Incorrect number of arguments, expecting %d";
	private static final String SUCCESS = "SUCCESS";

    @Override
    public Response mint(ChaincodeStub stub, List<String> args) {
        try {
            if (args.size() != 2) {
                throw new IllegalArgumentException(String.format(ARG_MESSAGE, 2));
            }

            String id = args.get(0);
            String type = "base";
            String owner = args.get(1);

            String caller = Address.getMyAddress(stub);
            if (!caller.equals(owner)) {
                throw new Exception("The caller should be an owner");
            }

            NFT nft = new NFT();
            nft.mint(stub, id, type, owner, null, null);
            return ResponseUtils.newSuccessResponse(SUCCESS);
        } catch (Exception e) {
            return ResponseUtils.newErrorResponse(e.getMessage());
        }
    }

    @Override
    public Response burn(ChaincodeStub stub, List<String> args) {
        try {
            if (args.size() != 1) {
                throw new IllegalArgumentException(String.format(ARG_MESSAGE, 1));
            }

            String id = args.get(0);

            NFT nft = NFT.read(stub, id);

            String caller = Address.getMyAddress(stub);
            String owner = nft.getOwner();
            if (!caller.equals(owner)) {
                throw new Exception("The caller should be an owner");
            }

            nft.burn(stub, id);

            return ResponseUtils.newSuccessResponse(SUCCESS);
        } catch (Exception e) {
            return ResponseUtils.newErrorResponse(e.getMessage());
        }
    }

    @Override
    public Response getType(ChaincodeStub stub, List<String> args) {
        try {
            if (args.size() != 1) {
                throw new IllegalArgumentException(String.format(ARG_MESSAGE, 1));
            }

            String id = args.get(0);

            NFT nft = NFT.read(stub, id);
            String type = nft.getType();

            return ResponseUtils.newSuccessResponse(type);
        } catch (Exception e) {
            return ResponseUtils.newErrorResponse(e.getMessage());
        }
    }

    @Override
    public Response setOwner(ChaincodeStub stub, List<String> args) {
        try {
            if (args.size() != 3) {
                throw new IllegalArgumentException(String.format(ARG_MESSAGE, 3));
            }

            String sender = args.get(0);
            String receiver = args.get(1);
            String id = args.get(2);

            NFT nft = NFT.read(stub, id);

            String owner = nft.getOwner();
            if (!sender.equals(owner)) {
                throw new Exception("The sender should be an owner");
            }

            String caller = Address.getMyAddress(stub);
            String approvee = nft.getApprovee();
            if ( !(caller.equals(owner) || caller.equals(approvee) || this.isOperatorForOwner(owner, caller)) ) {
                throw new Exception("The caller should be an owner or an operator");
            }

            nft.setOwner(stub, receiver);

            return ResponseUtils.newSuccessResponse(SUCCESS);
        } catch (Exception e) {
            return ResponseUtils.newErrorResponse(e.getMessage());
        }
    }

    @Override
    public Response getOwner(ChaincodeStub stub, List<String> args) {
        try {
            if (args.size() != 1) {
                throw new IllegalArgumentException(String.format(ARG_MESSAGE, 1));
            }

            String id = args.get(0);

            NFT nft = NFT.read(stub, id);
            String owner = nft.getOwner();

            return ResponseUtils.newSuccessResponse(owner);
        } catch (Exception e) {
            return ResponseUtils.newErrorResponse(e.getMessage());
        }
    }

    @Override
    public Response setOperatorForCaller(ChaincodeStub stub, List<String> args) {
        try {
            if (args.size() != 2) {
                throw new IllegalArgumentException(String.format(ARG_MESSAGE, 2));
            }

            String operator = args.get(0);
            boolean approved = Boolean.parseBoolean(args.get(1));

            String caller = Address.getMyAddress(stub);
            if (operator.equals(caller)) {
                throw new Exception("The Caller should not be an operator");
            }

            if (this.getOperatorsApproval() == null) {
                throw new Exception("OperatorsApproval does not exist");
            }

            this.setOperatorsApproval(stub, caller, operator, approved);

            return ResponseUtils.newSuccessResponse(SUCCESS);
        } catch (Exception e) {
            return ResponseUtils.newErrorResponse(e.getMessage());
        }
    }

    @Override
    public Response isOperatorForCaller(ChaincodeStub stub, List<String> args) {
        try {
            if (args.size() != 2) {
                throw new IllegalArgumentException(String.format(ARG_MESSAGE, 2));
            }

            String owner = args.get(0);
            String operator = args.get(1);

            boolean approved = this.isOperatorForOwner(owner, operator);

            return ResponseUtils.newSuccessResponse(Boolean.toString(approved).toUpperCase());
        } catch (Exception e) {
            return ResponseUtils.newErrorResponse(e.getMessage());
        }
    }

    @Override
    public Response setApprovee(ChaincodeStub stub, List<String> args) {
        try {
            if (args.size() != 2) {
                throw new IllegalArgumentException(String.format(ARG_MESSAGE, 2));
            }

            String approvee = args.get(0);
            String id = args.get(1);

            NFT nft = NFT.read(stub, id);

            String caller = Address.getMyAddress(stub);
            String owner = nft.getOwner();
            if ( !(caller.equals(owner) || this.isOperatorForOwner(owner, caller)) ) {
                throw new Exception("The caller should be an owner or an operator");
            }

            nft.setApprovee(stub, approvee);

            return ResponseUtils.newSuccessResponse(SUCCESS);
        } catch (Exception e) {
            return ResponseUtils.newErrorResponse(e.getMessage());
        }
    }

    @Override
    public Response getApprovee(ChaincodeStub stub, List<String> args) {
        try {
            if (args.size() != 1) {
                throw new IllegalArgumentException(String.format(ARG_MESSAGE, 1));
            }

            String id = args.get(0);

            NFT nft = NFT.read(stub, id);
            String approvee = nft.getApprovee();

            return ResponseUtils.newSuccessResponse(approvee);
        } catch (Exception e) {
            return ResponseUtils.newErrorResponse(e.getMessage());
        }
    }
}
