package kr.ac.postech.sslab.extension;

import kr.ac.postech.sslab.main.CustomChainCodeStub;
import kr.ac.postech.sslab.nft.NFT;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.math.BigInteger;
import java.util.*;
import kr.ac.postech.sslab.standard.*;
import org.hyperledger.fabric.shim.ledger.KeyModification;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

public class EERC721 extends ERC721 {
	private static final ChaincodeStub stub = CustomChainCodeStub.getChaincodeStub();

	private static final String DEACTIVATED_MESSAGE = "Deactivated token";
	private static final String BASE_TYPE_ERROR_MESSAGE = "Function '%s' is not allowed for token type 'base'";
	private static final String ACTIVATED_KEY = "activated";
	private static final String PARENT_KEY = "parent";
	private static final String CHILDREN_KEY = "children";
	private static final String BASE_TYPE = "base";

	public static BigInteger balanceOf(String owner, String type) throws Exception {
		String query = "{\"selector\":{\"owner\":\"" + owner + "\"}}";

		long ownedTokensCount = 0;
		QueryResultsIterator<KeyValue> resultsIterator = stub.getQueryResult(query);
		while(resultsIterator.iterator().hasNext()) {
			BigInteger tokenId = new BigInteger(resultsIterator.iterator().next().getKey());
			NFT nft = NFT.read(tokenId);

			boolean activated;
			if (nft.getType().equals(BASE_TYPE)) {
				activated = true;
			}
			else {
				activated = (boolean) nft.getXAttr(ACTIVATED_KEY);
			}

			if (activated && (type.equals("_") || type.equals(nft.getType()))) {
				ownedTokensCount++;
			}
		}

		return BigInteger.valueOf(ownedTokensCount);
	}

	public static List<BigInteger> tokenIdsOf(String owner) {
		String query = "{\"selector\":{\"owner\":\"" + owner + "\"}}";

		List<BigInteger> tokenIds = new ArrayList<>();
		QueryResultsIterator<KeyValue> resultsIterator = stub.getQueryResult(query);
		while(resultsIterator.iterator().hasNext()) {
			BigInteger tokenId = new BigInteger(resultsIterator.iterator().next().getKey());
			tokenIds.add(tokenId);
		}

		return tokenIds;
	}

	public static List<BigInteger> tokenIdsOf(String owner, String type) throws Exception {
		String query;
		QueryResultsIterator<KeyValue> resultsIterator;
		List<BigInteger> tokenIds = new ArrayList<>();
		boolean activated;
		if (type.equals("_")) {
			query = "{\"selector\":{\"owner\":\"" + owner + "\"}}";

			resultsIterator = stub.getQueryResult(query);
			while(resultsIterator.iterator().hasNext()) {
				BigInteger tokenId = new BigInteger(resultsIterator.iterator().next().getKey());
				NFT nft = NFT.read(tokenId);

				if (nft.getType().equals(BASE_TYPE)) {
					activated = true;
				} else {
					activated = (boolean) nft.getXAttr(ACTIVATED_KEY);
				}

				if (activated) {
					tokenIds.add(tokenId);
				}
			}
		} else {
			query = "{\"selector\":{\"owner\":\"" + owner + "\"," +
					"\"type\":\"" + type + "\"}}";

			resultsIterator = stub.getQueryResult(query);
			while (resultsIterator.iterator().hasNext()) {
				BigInteger tokenId = new BigInteger(resultsIterator.iterator().next().getKey());
				if (type.equals(BASE_TYPE)) {
					activated = true;
				} else {
					NFT nft = NFT.read(tokenId);
					activated = (boolean) nft.getXAttr(ACTIVATED_KEY);
				}

				if (activated) {
					tokenIds.add(tokenId);
				}
			}
		}

		return tokenIds;
	}

	public static boolean divide(BigInteger tokenId, List<BigInteger> newIds, List<Object> values, String index) throws Exception {
		if (newIds.size() != 2 || values.size() != 2) {
			throw new IndexOutOfBoundsException("Both array 'newIds' and 'values' should have only two elements");
		}

		NFT nft = NFT.read(tokenId);
		if (nft.getType().equals(BASE_TYPE)) {
			throw new Exception(String.format(BASE_TYPE_ERROR_MESSAGE, "divide"));
		}

		boolean activated = (boolean) nft.getXAttr(ACTIVATED_KEY);

		if (!activated) {
			throw new Exception(DEACTIVATED_MESSAGE);
		}

		List<NFT> children = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Map<String, Object> xattr = nft.getXAttr();
			Map<String, String> uri = nft.getURI();

			NFT child = new NFT();
			child.mint(newIds.get(i), nft.getType(), nft.getOwner(), xattr, uri);
			child.setXAttr(index, values.get(i));
			child.setXAttr(PARENT_KEY, nft.getId());
			children.add(child);
		}

		nft.setXAttr(ACTIVATED_KEY, false);
		nft.setXAttr(CHILDREN_KEY, newIds);

		return true;
	}

    public static boolean deactivate(BigInteger tokenId) throws Exception {
		NFT nft = NFT.read(tokenId);
		if (nft.getType().equals(BASE_TYPE)) {
			throw new Exception(String.format(BASE_TYPE_ERROR_MESSAGE, "deactivate"));
		}

		nft.setXAttr(ACTIVATED_KEY, false);

		return true;
	}

    public static String query(BigInteger tokenId) throws Exception {
		NFT nft = NFT.read(tokenId);
		String query = nft.toJSONString();
		return query;
	}

    public static List<String> queryHistory(BigInteger tokenId) {
		List<String> histories = new LinkedList<>();
		QueryResultsIterator<KeyModification> resultsIterator = stub.getHistoryForKey(tokenId.toString());
		while (resultsIterator.iterator().hasNext()) {
			histories.add(resultsIterator.iterator().next().getStringValue());
		}

		return histories;
	}
}
