package kr.ac.postech.sslab.extension;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.postech.sslab.main.CustomChaincodeBase;
import kr.ac.postech.sslab.nft.NFT;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyModification;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import static kr.ac.postech.sslab.constant.DataType.*;
import static kr.ac.postech.sslab.constant.Key.*;
import static kr.ac.postech.sslab.constant.Message.BASE_TYPE_ERROR_MESSAGE;
import static kr.ac.postech.sslab.constant.Message.DEACTIVATED_MESSAGE;

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
				activated = (boolean) nft.getXAttr(ACTIVATED_KEY);
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
				activated = (boolean) nft.getXAttr(ACTIVATED_KEY);
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
				activated = (boolean) nft.getXAttr(ACTIVATED_KEY);
			}

			if (activated) {
				tokenIds.add(tokenId);
			}
		}

		return tokenIds;
	}

	public static boolean divide(ChaincodeStub stub, BigInteger tokenId, List<BigInteger> newIds, List<String> values, String index)
			throws IndexOutOfBoundsException, IOException {
		if (newIds.size() != 2 || values.size() != 2) {
			throw new IndexOutOfBoundsException("Both array 'newIds' and 'values' should have only two elements");
		}

		NFT nft = NFT.read(stub, tokenId);
		if (nft.getType().equals(BASE_TYPE)) {
			LOG.error(BASE_TYPE_ERROR_MESSAGE);
			return false;
		}

		boolean activated = (boolean) nft.getXAttr(ACTIVATED_KEY);

		if (!activated) {
			LOG.error(DEACTIVATED_MESSAGE);
			return false;
		}

		List<NFT> children = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			String xattrJson = objectMapper.writeValueAsString(nft.getXAttr());
			Map<String, Object> xattr = objectMapper.readValue(xattrJson, new TypeReference<HashMap<String, Object>>() {});

			String uriJson = objectMapper.writeValueAsString(nft.getURI());
			Map<String, String> uri = objectMapper.readValue(uriJson, new TypeReference<HashMap<String, String>>() {});

			NFT child = new NFT();
			child.mint(stub, newIds.get(i), nft.getType(), nft.getOwner(), xattr, uri);

			Map<String, List<String>> tokenType = XType.getTokenType(nft.getType());
			String dataType = tokenType.get(index).get(0);
			switch (dataType) {
				case INTEGER: {
					int value = Integer.parseInt(values.get(i));
					child.setXAttr(stub, index, value);
					break;
				}

				case BIG_INTEGER: {
					BigInteger value = new BigInteger(values.get(1));
					child.setXAttr(stub, index, value);
					break;
				}

				case DOUBLE: {
					double value = Double.parseDouble(values.get(1));
					child.setXAttr(stub, index, value);
					break;
				}

				case BYTE: {
					byte value = Byte.parseByte(values.get(1));
					child.setXAttr(stub, index, value);
					break;
				}

				case STRING: {
					String value = values.get(1);
					child.setXAttr(stub, index, value);
					break;
				}

				default:
					LOG.error("No such data type");
					return false;
			}

			child.setXAttr(stub, PARENT_KEY, nft.getId());
			children.add(child);
		}

		nft.setXAttr(stub, ACTIVATED_KEY, false);
		nft.setXAttr(stub, CHILDREN_KEY, newIds);

		return true;
	}

    public static boolean deactivate(ChaincodeStub stub, BigInteger tokenId) throws IOException {
		NFT nft = NFT.read(stub, tokenId);
		if (nft.getType().equals(BASE_TYPE)) {
			LOG.error(BASE_TYPE_ERROR_MESSAGE);
			return false;
		}

		return nft.setXAttr(stub, ACTIVATED_KEY, false);
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
