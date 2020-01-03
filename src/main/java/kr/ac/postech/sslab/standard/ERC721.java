package kr.ac.postech.sslab.standard;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.postech.sslab.main.CustomChainCodeStub;
import kr.ac.postech.sslab.nft.NFT;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class ERC721 {
	private static ChaincodeStub stub = CustomChainCodeStub.getChaincodeStub();
	private static Map<String, Map<String, Boolean>> operatorsApproval = new HashMap<>();
	private static final String OPERATORS_APPROVAL = "operatorsApproval";

	public static BigInteger balanceOf(String owner) {
		String query = "{\"selector\":{\"owner\":\"" + owner + "\"}}";

		long ownedTokensCount = 0;
		QueryResultsIterator<KeyValue> resultsIterator = stub.getQueryResult(query);
		while(resultsIterator.iterator().hasNext()) {
			resultsIterator.iterator().next();
			ownedTokensCount++;
		}

		return BigInteger.valueOf(ownedTokensCount);
	}

	public static String ownerOf(BigInteger tokenId) throws Exception {
		NFT nft = NFT.read(tokenId);
		String owner = nft.getOwner();

		return owner;
	}

	public static boolean transferFrom(String from, String to, BigInteger tokenId) throws Exception {
			NFT nft = NFT.read(tokenId);

			String owner = nft.getOwner();
			if (!from.equals(owner)) {
				return false;
			}

			nft.setApprovee("");
			nft.setOwner(to);

			return true;
	}

	public static boolean approve(String approved, BigInteger tokenId) throws Exception {
		NFT nft = NFT.read(tokenId);
		nft.setApprovee(approved);

		return true;
	}

	public static boolean setApprovalForAll(String caller, String operator, boolean approved) throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		String operatorsApprovalString = stub.getStringState(OPERATORS_APPROVAL);
		operatorsApproval = mapper.readValue(operatorsApprovalString,
				new TypeReference<HashMap<String, HashMap<String, Boolean>>>() {});

		Map<String, Boolean> operatorMap;
		if (operatorsApproval.containsKey(caller)) {
			operatorMap = operatorsApproval.get(caller);
		}
		else {
			operatorMap = new HashMap<>();
		}

		operatorMap.put(operator, approved);
		operatorsApproval.put(caller, operatorMap);

		stub.putStringState(OPERATORS_APPROVAL, mapper.writeValueAsString(operatorsApproval));
		return true;
	}

    public static String getApproved(BigInteger tokenId) throws Exception {
		NFT nft = NFT.read(tokenId);
		String approved = nft.getApprovee();

		return approved;
	}

	public static boolean isApprovedForAll(String owner, String operator) {
		if (operatorsApproval.containsKey(owner)) {
			return operatorsApproval.get(owner).getOrDefault(operator, false);
		}
		else {
			return false;
		}
	}
}
