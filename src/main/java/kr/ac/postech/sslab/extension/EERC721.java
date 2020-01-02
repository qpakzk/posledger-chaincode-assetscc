package kr.ac.postech.sslab.extension;

import kr.ac.postech.sslab.adapter.XAttr;
import kr.ac.postech.sslab.nft.NFT;
import kr.ac.postech.sslab.type.URI;
import org.hyperledger.fabric.shim.ChaincodeStub;
import java.util.*;
import kr.ac.postech.sslab.standard.*;
import org.hyperledger.fabric.shim.ledger.KeyModification;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

public class EERC721 extends ERC721 implements IEERC721 {
	private static final String ARG_MESSAGE = "Incorrect number of arguments, expecting %d";
	private static final String SUCCESS = "SUCCESS";
	private static final String DEACTIVATED_MESSAGE = "Token %s is deactivated";
	private static final String BASE_TYPE_ERROR_MESSAGE = "Function '%s' is not allowed for token type 'base'";
	private static final String ACTIVATED_KEY = "activated";
	private static final String PARENT_KEY = "parent";
	private static final String CHILDREN_KEY = "children";
	private static final String BASE_TYPE = "base";

	@Override
	public Response balanceOf(ChaincodeStub stub, List<String> args) {
		try {
			if (args.size() == 1) {
				return super.balanceOf(stub, args);
			}

			if (args.size() != 2) {
				throw new IllegalArgumentException(String.format(ARG_MESSAGE + " or %d", 1, 2));
			}

			String owner = args.get(0);
			String type = args.get(1);

			String query = "{\"selector\":{\"owner\":\"" + owner + "\"}}";

			long ownedTokensCount = 0;
			QueryResultsIterator<KeyValue> resultsIterator = stub.getQueryResult(query);
			while(resultsIterator.iterator().hasNext()) {
				String id = resultsIterator.iterator().next().getKey();
				NFT nft = NFT.read(stub, id);

				boolean activated = true;
				if (!nft.getType().equals(BASE_TYPE)) {
					activated = Boolean.parseBoolean(nft.getXAttr(ACTIVATED_KEY));
				}
				if (activated && nft.getType().equals(type)) {
					ownedTokensCount++;
				}
			}

			return newSuccessResponse(Long.toString(ownedTokensCount));
		} catch (Exception e) {
			return newErrorResponse(e.getMessage());
		}
	}

	@Override
	public Response tokenIdsOf(ChaincodeStub stub, List<String> args) {
		if (args.size() == 1) {
			return this.tokenIdsOfForAll(stub, args.get(0));
		}
		else if (args.size() == 2) {
			return this.tokenIdsOfForType(stub, args.get(0), args.get(1));
		}
		else {
			throw new IllegalArgumentException(String.format(ARG_MESSAGE + " or %d", 1, 2));
		}
	}

	private Response tokenIdsOfForAll(ChaincodeStub stub, String owner) {
		try {
			String query = "{\"selector\":{\"owner\":\"" + owner + "\"}}";

			List<String> tokenIds = new ArrayList<>();
			QueryResultsIterator<KeyValue> resultsIterator = stub.getQueryResult(query);
			while(resultsIterator.iterator().hasNext()) {
				String id = resultsIterator.iterator().next().getKey();
				NFT nft = NFT.read(stub, id);

				if (nft.getType().equals(BASE_TYPE)) {
					tokenIds.add(id);
				} else {
					boolean activated = Boolean.getBoolean(nft.getXAttr(ACTIVATED_KEY));
					if (activated) {
						tokenIds.add(id);
					}
				}
			}

			String result = tokenIds.toString();
			return newSuccessResponse(result);
		} catch (Exception e) {
			return newErrorResponse(e.getMessage());
		}
	}

	private Response tokenIdsOfForType(ChaincodeStub stub, String owner, String type) {
		try {
			String query = "{\"selector\":{\"owner\":\"" + owner + "\"," +
											"\"type\":\"" + type + "\"}}";

			List<String> tokenIds = new ArrayList<>();
			QueryResultsIterator<KeyValue> resultsIterator = stub.getQueryResult(query);
			boolean activated = true;
			while(resultsIterator.iterator().hasNext()) {
				String id = resultsIterator.iterator().next().getKey();
				if (!type.equals(BASE_TYPE)) {
					NFT nft = NFT.read(stub, id);
					activated = Boolean.getBoolean(nft.getXAttr(ACTIVATED_KEY));
				}

				if (activated) {
					tokenIds.add(id);
				}
			}

			String result = tokenIds.toString();
			return newSuccessResponse(result);
		} catch (Exception e) {
			return newErrorResponse(e.getMessage());
		}
	}
	@Override
	public Response divide(ChaincodeStub stub, List<String> args) {
		try {
			if (args.size() != 4) {
				throw new IllegalArgumentException(String.format(ARG_MESSAGE, 4));
			}

			String id = args.get(0);
			String[] newIds = args.get(1)
					.replace("[", "").replace("]", "").split(", ");
			String[] values = args.get(2)
					.replace("[", "").replace("]", "").split(", ");
			String index = args.get(3);

			if (newIds.length != 2 || values.length != 2) {
				throw new IndexOutOfBoundsException("Both array 'newIds' and 'values' should have only two elements");
			}

			NFT nft = NFT.read(stub, id);
			if (nft.getType().equals(BASE_TYPE)) {
				return newErrorResponse(String.format(BASE_TYPE_ERROR_MESSAGE, "divide"));
			}

			boolean activated = Boolean.parseBoolean(nft.getXAttr(ACTIVATED_KEY));

			if (!activated) {
				return newErrorResponse(String.format(DEACTIVATED_MESSAGE, id));
			}

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
				child[i].setXAttr(stub, PARENT_KEY, nft.getId());
			}

			nft.setXAttr(stub, ACTIVATED_KEY, "false");
			nft.setXAttr(stub, CHILDREN_KEY, newIds[0] + "," + newIds[1]);

			return newSuccessResponse(SUCCESS);
		} catch (Exception e) {
			return newErrorResponse(e.getMessage());
		}
	}

	@Override
    public Response deactivate(ChaincodeStub stub, List<String> args) {
		try {
			if (args.size() != 1) {
				throw new IllegalArgumentException(String.format(ARG_MESSAGE, 1));
			}

			String id = args.get(0);

			NFT nft = NFT.read(stub, id);
			if (nft.getType().equals(BASE_TYPE)) {
				return newErrorResponse(String.format(BASE_TYPE_ERROR_MESSAGE, "deactivate"));
			}

			nft.setXAttr(stub, ACTIVATED_KEY, "false");

			return newSuccessResponse(SUCCESS);
		} catch (Exception e) {
			return newErrorResponse(e.getMessage());
		}
	}

	@Override
    public Response query(ChaincodeStub stub, List<String> args) {
		try {
			if (args.size() != 1) {
				throw new IllegalArgumentException(String.format(ARG_MESSAGE, 1));
			}

			String id = args.get(0);

			NFT nft = NFT.read(stub, id);

			String query = nft.toJSONString();
			return newSuccessResponse(query);
		} catch (Exception e) {
			return newErrorResponse(e.getMessage());
		}
	}

	@Override
    public Response queryHistory(ChaincodeStub stub, List<String> args) {
		try {
			if (args.size() != 1) {
				throw new IllegalArgumentException(String.format(ARG_MESSAGE, 1));
			}

			String id = args.get(0);

			List<String> histories = new LinkedList<>();
			QueryResultsIterator<KeyModification> resultsIterator = stub.getHistoryForKey(id);
			while (resultsIterator.iterator().hasNext()) {
				histories.add(resultsIterator.iterator().next().getStringValue());
			}

			return newSuccessResponse(histories.toString());
		} catch (Exception e) {
			return newErrorResponse(e.getMessage());
		}
	}
}
