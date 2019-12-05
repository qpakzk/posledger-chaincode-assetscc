package kr.ac.postech.sslab.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.postech.sslab.adapter.XAttr;
import kr.ac.postech.sslab.nft.NFT;
import kr.ac.postech.sslab.type.URI;
import kr.ac.postech.sslab.user.Address;
import org.hyperledger.fabric.shim.ChaincodeStub;
import java.io.IOException;
import java.util.*;
import kr.ac.postech.sslab.standard.*;
import org.hyperledger.fabric.shim.ledger.KeyModification;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

public class EERC721 extends ERC721 implements IEERC721 {
	@Override
	public Response balanceOf(ChaincodeStub stub, List<String> args) {
		try {
			if (args.size() == 1) {
				return super.balanceOf(stub, args);
			}

			if (args.size() != 2) {
				throw new Throwable("FAILURE");
			}

			String owner = args.get(0);
			String type = args.get(1);

			long ownedTokensCount = this.getBalance(stub, owner, type);

			return newSuccessResponse(Long.toString(ownedTokensCount));
		} catch (Throwable throwable) {
			return newErrorResponse("FAILURE");
		}
	}

	private long getBalance(ChaincodeStub stub, String owner, String type) throws IOException {
		String query = "{\"selector\":{\"owner\":\"" + owner + "\"}}";

		long ownedTokensCount = 0;
		QueryResultsIterator<KeyValue> resultsIterator = stub.getQueryResult(query);
		while(resultsIterator.iterator().hasNext()) {
			String id = resultsIterator.iterator().next().getKey();
			NFT nft = NFT.read(stub, id);

			if (nft.getType().equals(type)) {
				ownedTokensCount++;
			}
		}

		return ownedTokensCount;
	}

	@Override
	public Response divide(ChaincodeStub stub, List<String> args) {
		try {
			if (args.size() != 4) {
				throw new Throwable("FAILURE");
			}

			String id = args.get(0);
			String[] newIds = args.get(1)
					.replace("[", "").replace("]", "").split(", ");
			String[] values = args.get(2)
					.replace("[", "").replace("]", "").split(", ");
			String index = args.get(3);

			if (newIds.length != 2 || values.length != 2) {
				throw new Throwable();
			}

			NFT nft = NFT.read(stub, id);

			String caller = Address.getMyAddress(stub);
			String owner = nft.getOwner();

			if ( !(caller.equals(owner) || this.isOperatorForOwner(owner, caller)) )
				throw new Throwable();

			NFT[] child = new NFT[2];

			for (int i = 0; i < 2; i++) {
				child[i] = new NFT();

				Map<String, Object> xattrMap = nft.getXAttr().toMap();
				XAttr xattr = new XAttr();
				xattr.assign(nft.getType(), xattrMap);

				Map<String, String> uriMap = nft.getURI().toMap();
				URI uri = new URI();
				uri.assign(uriMap);

				child[i].mint(stub, newIds[i], nft.getType(), nft.getOwner(), xattr, uri);
				child[i].setXAttr(stub, index, values[i]);
				child[i].setXAttr(stub, "parent", nft.getId());
			}

			nft.setXAttr(stub, "activated", "false");
			nft.setXAttr(stub, "children", newIds[0] + "," + newIds[1]);

			return newSuccessResponse("SUCCESS");
		} catch (Throwable throwable) {
			return newErrorResponse("FAILURE");
		}
	}

	@Override
    public Response deactivate(ChaincodeStub stub, List<String> args) {
		try {
			if (args.size() != 1) {
				throw new Throwable("FAILURE");
			}

			String id = args.get(0);

			NFT nft = NFT.read(stub, id);

			String caller = Address.getMyAddress(stub);
			String owner = nft.getOwner();

			if ( !(caller.equals(owner) || this.isOperatorForOwner(owner, caller)) )
				throw new Throwable();

			nft.setXAttr(stub, "activated", "false");

			return newSuccessResponse("SUCCESS");
		} catch (Throwable throwable) {
			return newErrorResponse("FAILURE");
		}
	}

	@Override
    public Response query(ChaincodeStub stub, List<String> args) {
		try {
			if (args.size() != 1) {
				throw new Throwable("FAILURE");
			}

			String id = args.get(0);

			NFT nft = NFT.read(stub, id);

			String query = nft.toJSONString();
			return newSuccessResponse(query);
		} catch (Throwable throwable) {
			return newErrorResponse("FAILURE");
		}
	}

	@Override
    public Response queryHistory(ChaincodeStub stub, List<String> args) {
		try {
			if (args.size() != 1) {
				throw new Throwable("FAILURE");
			}

			String id = args.get(0);

			List<String> histories = new LinkedList<>();
			QueryResultsIterator<KeyModification> resultsIterator = stub.getHistoryForKey(id);
			while (resultsIterator.iterator().hasNext()) {
				histories.add(resultsIterator.iterator().next().getStringValue());
			}

			return newSuccessResponse(histories.toString());
		} catch (Throwable throwable) {
			return newErrorResponse("FAILURE");
		}
	}
}