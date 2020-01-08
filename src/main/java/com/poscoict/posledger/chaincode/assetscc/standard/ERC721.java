package com.poscoict.posledger.chaincode.assetscc.standard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poscoict.posledger.chaincode.assetscc.main.CustomChaincodeBase;
import com.poscoict.posledger.chaincode.assetscc.structure.NFT;
import com.poscoict.posledger.chaincode.assetscc.structure.OperatorsApproval;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static com.poscoict.posledger.chaincode.assetscc.constant.Key.OPERATORS_APPROVAL;

public class ERC721 extends CustomChaincodeBase {
	private static ObjectMapper objectMapper = new ObjectMapper();

	private static final String QUERY_OWNER = "{\"selector\":{\"owner\":\"%s\"}}";

	public static BigInteger balanceOf(ChaincodeStub stub, String owner) {
		String query = String.format(QUERY_OWNER, owner);

		long ownedTokensCount = 0;
		QueryResultsIterator<KeyValue> resultsIterator = stub.getQueryResult(query);
		while(resultsIterator.iterator().hasNext()) {
			resultsIterator.iterator().next();
			ownedTokensCount++;
		}

		return BigInteger.valueOf(ownedTokensCount);
	}

	public static String ownerOf(ChaincodeStub stub, BigInteger tokenId) throws IOException {
		NFT nft = NFT.read(stub, tokenId);
		return nft.getOwner();
	}

	public static boolean transferFrom(ChaincodeStub stub, String from, String to, BigInteger tokenId) throws IOException {
			NFT nft = NFT.read(stub, tokenId);

			String owner = nft.getOwner();
			if (!from.equals(owner)) {
				return false;
			}

			nft.setApprovee(stub, "");
			nft.setOwner(stub, to);
			return true;
	}

	public static boolean approve(ChaincodeStub stub, String approved, BigInteger tokenId) throws IOException {
		NFT nft = NFT.read(stub, tokenId);
		return nft.setApprovee(stub, approved);
	}

	public static boolean setApprovalForAll(ChaincodeStub stub, String caller, String operator, boolean approved) throws IOException {
		OperatorsApproval approval = OperatorsApproval.read(stub);
		Map<String, Map<String, Boolean>> operators = approval.getOperatorsApproval();

		Map<String, Boolean> map;
		if (operators.containsKey(caller)) {
			map = operators.get(caller);
		}
		else {
			map = new HashMap<>();
		}

		map.put(operator, approved);
		operators.put(caller, map);

		approval.setOperatorsApproval(stub, operators);
		return true;
	}

    public static String getApproved(ChaincodeStub stub, BigInteger tokenId) throws IOException {
		NFT nft = NFT.read(stub, tokenId);
		return nft.getApprovee();
	}

	public static boolean isApprovedForAll(ChaincodeStub stub, String owner, String operator) throws IOException {
		OperatorsApproval approval = OperatorsApproval.read(stub);
		Map<String, Map<String, Boolean>> operators = approval.getOperatorsApproval();

		if (operators.containsKey(owner)) {
			return operators.get(owner).getOrDefault(operator, false);
		}
		else {
			return false;
		}
	}
}
