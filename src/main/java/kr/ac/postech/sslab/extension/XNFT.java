package kr.ac.postech.sslab.extension;

import kr.ac.postech.sslab.adapter.XAttr;
import kr.ac.postech.sslab.nft.NFT;
import kr.ac.postech.sslab.standard.BaseNFT;
import kr.ac.postech.sslab.type.URI;
import kr.ac.postech.sslab.user.Address;
import kr.ac.postech.sslab.exception.NoMatchException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ResponseUtils;

import java.util.List;

public class XNFT extends BaseNFT implements IXNFT {
	private static final String ARG_MESSAGE = "Incorrect number of arguments, expecting %d";
	private static final String SUCCESS = "SUCCESS";

    @Override
    public Response setURI(ChaincodeStub stub, List<String> args) {
        try {
            if (args.size() != 3) {
                throw new IllegalArgumentException(String.format(ARG_MESSAGE, 3));
            }

            String id = args.get(0);
            String index = args.get(1);
            String value = args.get(2);

            NFT nft = NFT.read(stub, id);

            String caller = Address.getMyAddress(stub);
            String owner = nft.getOwner();
            if (!caller.equals(owner))
                throw new NoMatchException("The caller should be an owner");


            URI uri = nft.getURI();
            if (uri == null)
                throw new NullPointerException();

            nft.setURI(stub, index, value);

            return ResponseUtils.newSuccessResponse(SUCCESS);
        } catch (Exception e) {
            return ResponseUtils.newErrorResponse(e.getMessage());
        }
    }

    @Override
    public Response getURI(ChaincodeStub stub, List<String> args) {
        try {
            if (args.size() != 2) {
                throw new IllegalArgumentException(String.format(ARG_MESSAGE, 2));
            }

            String id = args.get(0);
            String index = args.get(1);
            NFT nft = NFT.read(stub, id);

            URI uri = nft.getURI();
            if (uri == null)
                throw new NullPointerException();

            String value = nft.getURI(index);

            if (value == null)
                throw new NullPointerException();

            return ResponseUtils.newSuccessResponse(value);
        } catch (Exception e) {
            return ResponseUtils.newErrorResponse(e.getMessage());
        }
    }

    @Override
    public Response setXAttr(ChaincodeStub stub, List<String> args) {
        try {
            if (args.size() != 3) {
                throw new IllegalArgumentException(String.format(ARG_MESSAGE, 3));
            }

            String id = args.get(0);
            String index = args.get(1);
            String value  = args.get(2);

            NFT nft = NFT.read(stub, id);

            XAttr xattr = nft.getXAttr();
            if (xattr == null)
                throw new NullPointerException();

            String caller = Address.getMyAddress(stub);
            String owner = nft.getOwner();
            if (!caller.equals(owner))
                throw new NoMatchException("The caller should be an owner");

            nft.setXAttr(stub, index, value);

            return ResponseUtils.newSuccessResponse(SUCCESS);
        } catch (Exception e) {
            return ResponseUtils.newErrorResponse(e.getMessage());
        }
    }

    @Override
    public Response getXAttr(ChaincodeStub stub, List<String> args) {
        try {
            if (args.size() != 2) {
                throw new IllegalArgumentException(String.format(ARG_MESSAGE, 2));
            }

            String id = args.get(0);
            String index = args.get(1);

            NFT nft = NFT.read(stub, id);

            XAttr xattr = nft.getXAttr();
            if (xattr == null)
                throw new NullPointerException();

            String value = nft.getXAttr(index);
            return ResponseUtils.newSuccessResponse(value);
        } catch (Exception e) {
            return ResponseUtils.newErrorResponse(e.getMessage());
        }
    }
}
