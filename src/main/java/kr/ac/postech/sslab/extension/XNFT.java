package kr.ac.postech.sslab.extension;

import kr.ac.postech.sslab.adapter.XAttr;
import kr.ac.postech.sslab.nft.NFT;
import kr.ac.postech.sslab.standard.BaseNFT;
import kr.ac.postech.sslab.type.URI;
import kr.ac.postech.sslab.user.Address;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.List;

public class XNFT extends BaseNFT implements IXNFT {
    @Override
    public Response setURI(ChaincodeStub stub, List<String> args) {
        try {
            if (args.size() != 3) {
                throw new Throwable("FAILURE");
            }

            String id = args.get(0);
            String index = args.get(1);
            String value = args.get(2);

            NFT nft = NFT.read(stub, id);

            String caller = Address.getMyAddress(stub);
            String owner = nft.getOwner();
            if (!caller.equals(owner))
                throw new Throwable();


            URI uri = nft.getURI();
            if (uri == null)
                throw new Throwable();

            nft.setURI(stub, index, value);

            return newSuccessResponse("SUCCESS");
        } catch (Throwable throwable) {
            return newErrorResponse("FAILURE");
        }
    }

    @Override
    public Response getURI(ChaincodeStub stub, List<String> args) {
        try {
            String id = args.get(0);
            String index = args.get(1);
            NFT nft = NFT.read(stub, id);

            URI uri = nft.getURI();
            if (uri == null)
                throw new Throwable();

            String value = nft.getURI(index);

            if (value == null)
                throw new Throwable();

            return newSuccessResponse(value);
        } catch (Throwable throwable) {
            return newErrorResponse("FAILURE");
        }
    }

    @Override
    public Response setXAttr(ChaincodeStub stub, List<String> args) {
        try {
            if (args.size() != 3) {
                throw new Throwable("FAILURE");
            }

            String id = args.get(0);
            String index = args.get(1);
            String value  = args.get(2);

            NFT nft = NFT.read(stub, id);

            XAttr xattr = nft.getXAttr();
            if (xattr == null)
                throw new Throwable();

            String caller = Address.getMyAddress(stub);
            String owner = nft.getOwner();
            if (!caller.equals(owner))
                throw new Throwable();

            nft.setXAttr(stub, index, value);

            return newSuccessResponse("SUCCESS");
        } catch (Throwable throwable) {
            return newErrorResponse("FAILURE");
        }
    }

    @Override
    public Response getXAttr(ChaincodeStub stub, List<String> args) {
        try {
            if (args.size() != 2) {
                throw new Throwable("FAILURE");
            }

            String id = args.get(0);
            String index = args.get(1);

            NFT nft = NFT.read(stub, id);

            XAttr xattr = nft.getXAttr();
            if (xattr == null)
                throw new Throwable();

            String value = nft.getXAttr(index);
            return newSuccessResponse(value);
        } catch (Throwable throwable) {
            return newErrorResponse("FAILURE");
        }
    }
}
