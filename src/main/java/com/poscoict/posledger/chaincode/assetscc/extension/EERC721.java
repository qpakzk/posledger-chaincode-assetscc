package com.poscoict.posledger.chaincode.assetscc.extension;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poscoict.posledger.chaincode.assetscc.constant.Key;
import com.poscoict.posledger.chaincode.assetscc.constant.Message;
import com.poscoict.posledger.chaincode.assetscc.main.CustomChaincodeBase;
import com.poscoict.posledger.chaincode.assetscc.structure.NFT;
import com.poscoict.posledger.chaincode.assetscc.structure.TokenTypeManager;
import com.poscoict.posledger.chaincode.assetscc.util.DataTypeConversion;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyModification;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

public class EERC721 extends CustomChaincodeBase {
	private static final ObjectMapper objectMapper = new ObjectMapper();

	private static final Log LOG = LogFactory.getLog(EERC721.class);

	private static final String BASE_TYPE = "base";

	private static final String QUERY_OWNER = "{\"selector\":{\"owner\":\"%s\"}}";

	public static BigInteger balanceOf(ChaincodeStub stub, String owner, String type) throws IOException {
		String query = String.format(QUERY_OWNER, owner);

		long ownedTokensCount = 0;
		QueryResultsIterator<KeyValue> resultsIterator = stub.getQueryResult(query);
		while(resultsIterator.iterator().hasNext()) {
			BigInteger tokenId = new BigInteger(resultsIterator.iterator().next().getKey());
			NFT nft = NFT.read(stub, tokenId);

			boolean activated;
			if (nft.getType().equals(BASE_TYPE)) {
				activated = true;
			}
			else {
				activated = (boolean) nft.getXAttr(Key.ACTIVATED_KEY);
			}

			if (activated && (type.equals("_") || type.equals(nft.getType()))) {
				ownedTokensCount++;
			}
		}

		return BigInteger.valueOf(ownedTokensCount);
	}

	public static List<BigInteger> tokenIdsOf(ChaincodeStub stub, String owner) {
		String query = String.format(QUERY_OWNER, owner);

		List<BigInteger> tokenIds = new ArrayList<>();
		QueryResultsIterator<KeyValue> resultsIterator = stub.getQueryResult(query);
		while(resultsIterator.iterator().hasNext()) {
			BigInteger tokenId = new BigInteger(resultsIterator.iterator().next().getKey());
			tokenIds.add(tokenId);
		}

		return tokenIds;
	}

	public static List<BigInteger> tokenIdsOf(ChaincodeStub stub, String owner, String type) throws IOException {
		List<BigInteger> tokenIds;
		if (type.equals("_")) {
			tokenIds = tokenIdsOfForAllActivated(stub, owner);
		} else {
			tokenIds = tokenIdsOfForType(stub, owner, type);
		}

		return tokenIds;
	}

	private static List<BigInteger> tokenIdsOfForAllActivated(ChaincodeStub stub, String owner) throws IOException {
		List<BigInteger> tokenIds = new ArrayList<>();

		String query = String.format(QUERY_OWNER, owner);
		QueryResultsIterator<KeyValue> resultsIterator = stub.getQueryResult(query);
		while(resultsIterator.iterator().hasNext()) {
			BigInteger tokenId = new BigInteger(resultsIterator.iterator().next().getKey());
			NFT nft = NFT.read(stub, tokenId);

			boolean activated;
			if (nft.getType().equals(BASE_TYPE)) {
				activated = true;
			} else {
				activated = (boolean) nft.getXAttr(Key.ACTIVATED_KEY);
			}

			if (activated) {
				tokenIds.add(tokenId);
			}
		}

		return tokenIds;
	}

	private static List<BigInteger> tokenIdsOfForType(ChaincodeStub stub, String owner, String type) throws IOException {
		List<BigInteger> tokenIds = new ArrayList<>();

		String query = "{\"selector\":{\"owner\":\"" + owner + "\"," +
				"\"type\":\"" + type + "\"}}";

		QueryResultsIterator<KeyValue> resultsIterator = stub.getQueryResult(query);
		while (resultsIterator.iterator().hasNext()) {
			BigInteger tokenId = new BigInteger(resultsIterator.iterator().next().getKey());

			boolean activated;
			if (type.equals(BASE_TYPE)) {
				activated = true;
			} else {
				NFT nft = NFT.read(stub, tokenId);
				activated = (boolean) nft.getXAttr(Key.ACTIVATED_KEY);
			}

			if (activated) {
				tokenIds.add(tokenId);
			}
		}

		return tokenIds;
	}

	public static boolean divide(ChaincodeStub stub, BigInteger tokenId, List<BigInteger> newIds, List<String> values, String index)
			throws IOException {
		if (newIds.size() != 2 || values.size() != 2) {
			throw new IndexOutOfBoundsException("Both array 'newIds' and 'values' should have only two elements");
		}

		NFT nft = NFT.read(stub, tokenId);
		if (nft.getType().equals(BASE_TYPE)) {
			LOG.error(Message.BASE_TYPE_ERROR_MESSAGE);
			return false;
		}

		boolean activated = (boolean) nft.getXAttr(Key.ACTIVATED_KEY);

		if (!activated) {
			LOG.error(Message.DEACTIVATED_MESSAGE);
			return false;
		}

		for (int i = 0; i < 2; i++) {
			String xattrJson = objectMapper.writeValueAsString(nft.getXAttr());
			Map<String, Object> xattr = objectMapper.readValue(xattrJson, new TypeReference<HashMap<String, Object>>() {});

			String uriJson = objectMapper.writeValueAsString(nft.getURI());
			Map<String, String> uri = objectMapper.readValue(uriJson, new TypeReference<HashMap<String, String>>() {});

			NFT child = new NFT();
			child.mint(stub, newIds.get(i), nft.getType(), nft.getOwner(), xattr, uri);

			TokenTypeManager manager = TokenTypeManager.read(stub);
			String dataType = manager.getAttributeOfTokenType(nft.getType(), index).get(0);
			Object value = DataTypeConversion.strToDataType(dataType, values.get(i));
			if (value == null) {
				return false;
			}

			child.setXAttr(stub, index, value);
			child.setXAttr(stub, Key.PARENT_KEY, nft.getId());
		}

		nft.setXAttr(stub, Key.ACTIVATED_KEY, false);
		nft.setXAttr(stub, Key.CHILDREN_KEY, newIds);

		return true;
	}

    public static boolean deactivate(ChaincodeStub stub, BigInteger tokenId) throws IOException {
		NFT nft = NFT.read(stub, tokenId);
		if (nft.getType().equals(BASE_TYPE)) {
			LOG.error(Message.BASE_TYPE_ERROR_MESSAGE);
			return false;
		}

		return nft.setXAttr(stub, Key.ACTIVATED_KEY, false);
	}

    public static String query(ChaincodeStub stub, BigInteger tokenId) throws IOException {
		NFT nft = NFT.read(stub, tokenId);
		return nft.toJSONString();
	}

    public static List<String> queryHistory(ChaincodeStub stub, BigInteger tokenId) {
		List<String> histories = new LinkedList<>();
		QueryResultsIterator<KeyModification> resultsIterator = stub.getHistoryForKey(tokenId.toString());
		while (resultsIterator.iterator().hasNext()) {
			histories.add(resultsIterator.iterator().next().getStringValue());
		}

		return histories;
	}
}
