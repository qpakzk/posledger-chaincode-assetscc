package kr.ac.postech.sslab.extension;

import kr.ac.postech.sslab.adapter.XAttr;
import kr.ac.postech.sslab.nft.NFT;
import kr.ac.postech.sslab.type.URI;
import kr.ac.postech.sslab.user.Address;
import org.hyperledger.fabric.shim.ChaincodeStub;
import java.util.ArrayList;
import java.util.List;

public class DocNFT extends XNFT {
    @Override
    public Response mint(ChaincodeStub stub, List<String> args) {
        try {
            if (args.size() != 8) {
                throw new Throwable("FAILURE");
            }

            String id = args.get(0);
            String type = args.get(1);
            String owner = args.get(2);
            String pages = args.get(3);
            String hash = args.get(4);
            String signers = args.get(5);
            String path = args.get(6);
            String merkleroot = args.get(7);

            String caller = Address.getMyAddress(stub);
            if (!caller.equals(owner))
                throw new Throwable();

            XAttr xattr = new XAttr();
            ArrayList<String> params = new ArrayList<>();
            params.add(pages);
            params.add(hash);
            params.add(signers);

            xattr.assign(type, params);

            URI uri = new URI(path, merkleroot);

            NFT nft = new NFT();
            nft.mint(stub, id, type, owner, xattr, uri);

            return newSuccessResponse("SUCCESS");
        } catch (Throwable throwable) {
            return newErrorResponse("FAILURE");
        }
    }
}
