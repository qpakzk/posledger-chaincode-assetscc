package kr.ac.postech.sslab.standard;

import kr.ac.postech.sslab.main.ConcreteChaincodeBase;
import kr.ac.postech.sslab.nft.NFT;
import kr.ac.postech.sslab.user.Address;
import org.hyperledger.fabric.shim.ChaincodeStub;
import java.util.List;

public class BaseNFT extends ConcreteChaincodeBase implements IBaseNFT {
    @Override
    public Response mint(ChaincodeStub stub, List<String> args) {
        try {
            if (args.size() != 2) {
                throw new Throwable("FAILURE");
            }

            String id = args.get(0);
            String type = "base";
            String owner = args.get(1);

            String caller = Address.getMyAddress(stub);
            if (!caller.equals(owner)) {
                throw new Throwable();
            }

            NFT nft = new NFT();
            nft.mint(stub, id, type, owner, null, null);
            return newSuccessResponse("SUCCESS");
        } catch (Throwable throwable) {
            return newErrorResponse("FAILURE");
        }
    }

    @Override
    public Response burn(ChaincodeStub stub, List<String> args) {
        try {
            if (args.size() != 1) {
                throw new Throwable("FAILURE");
            }

            String id = args.get(0);

            NFT nft = NFT.read(stub, id);

            String caller = Address.getMyAddress(stub);
            String owner = nft.getOwner();
            if (!caller.equals(owner)) {
                throw new Throwable();
            }

            nft.burn(stub, id);

            return newSuccessResponse("SUCCESS");
        } catch (Throwable throwable) {
            return newErrorResponse("FAILURE");
        }
    }

    @Override
    public Response getType(ChaincodeStub stub, List<String> args) {
        try {
            if (args.size() != 1) {
                throw new Throwable("FAILURE");
            }

            String id = args.get(0);

            NFT nft = NFT.read(stub, id);
            String type = nft.getType();

            return newSuccessResponse(type);
        } catch (Throwable throwable) {
            return newErrorResponse("FAILURE");
        }
    }

    @Override
    public Response setOwner(ChaincodeStub stub, List<String> args) {
        try {
            if (args.size() != 3) {
                throw new Throwable("FAILURE");
            }

            String sender = args.get(0);
            String receiver = args.get(1);
            String id = args.get(2);

            NFT nft = NFT.read(stub, id);

            String owner = nft.getOwner();
            if (!sender.equals(owner)) {
                throw new Throwable();
            }

            String caller = Address.getMyAddress(stub);
            String approvee = nft.getApprovee();
            if ( !(caller.equals(owner) || caller.equals(approvee) || this.isOperatorForOwner(owner, caller)) ) {
                throw new Throwable();
            }

            nft.setOwner(stub, receiver);

            return newSuccessResponse("SUCCESS");
        } catch (Throwable throwable) {
            return newErrorResponse("FAILURE");
        }
    }

    @Override
    public Response getOwner(ChaincodeStub stub, List<String> args) {
        try {
            if (args.size() != 1) {
                throw new Throwable("FAILURE");
            }

            String id = args.get(0);

            NFT nft = NFT.read(stub, id);
            String owner = nft.getOwner();

            return newSuccessResponse(owner);
        } catch (Throwable throwable) {
            return newErrorResponse("FAILURE");
        }
    }

    @Override
    public Response setOperatorForCaller(ChaincodeStub stub, List<String> args) {
        try {
            if (args.size() != 2) {
                throw new Throwable("FAILURE");
            }

            String operator = args.get(0);
            boolean approved = Boolean.parseBoolean(args.get(1));

            String caller = Address.getMyAddress(stub);
            if (operator.equals(caller)) {
                throw new Throwable("FAILURE");
            }

            if (this.getOperatorsApproval() == null) {
                throw new Throwable();
            }

            this.setOperatorsApproval(stub, caller, operator, approved);

            return newSuccessResponse("SUCCESS");
        } catch (Throwable throwable) {
            return newErrorResponse("FAILURE");
        }
    }

    @Override
    public Response isOperatorForCaller(ChaincodeStub stub, List<String> args) {
        try {
            if (args.size() != 2) {
                throw new Throwable("FAILURE");
            }

            String owner = args.get(0);
            String operator = args.get(1);

            boolean approved = this.isOperatorForOwner(owner, operator);

            return newSuccessResponse(Boolean.toString(approved).toUpperCase());
        } catch (Throwable throwable) {
            return newErrorResponse("FAILURE");
        }
    }

    @Override
    public Response setApprovee(ChaincodeStub stub, List<String> args) {
        try {
            if (args.size() != 2) {
                throw new Throwable("FAILURE");
            }

            String approvee = args.get(0);
            String id = args.get(1);

            NFT nft = NFT.read(stub, id);

            String caller = Address.getMyAddress(stub);
            String owner = nft.getOwner();
            if ( !(caller.equals(owner) || this.isOperatorForOwner(owner, caller)) ) {
                throw new Throwable();
            }

            nft.setApprovee(stub, approvee);

            return newSuccessResponse("SUCCESS");
        } catch (Throwable throwable) {
            return newErrorResponse("FAILURE");
        }
    }

    @Override
    public Response getApprovee(ChaincodeStub stub, List<String> args) {
        try {
            if (args.size() != 1) {
                throw new Throwable("FAILURE");
            }

            String id = args.get(0);

            NFT nft = NFT.read(stub, id);
            String approvee = nft.getApprovee();

            return newSuccessResponse(approvee);
        } catch (Throwable throwable) {
            return newErrorResponse("FAILURE");
        }
    }
}