package kr.ac.postech.sslab.standard;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.postech.sslab.main.CustomChaincodeBase;
import kr.ac.postech.sslab.nft.NFT;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class ERC721 extends CustomChaincodeBase {
	private static final String OPERATORS_APPROVAL = "operatorsApproval";

	public static BigInteger balanceOf(ChaincodeStub stub, String owner) {
		String query = "{\"selector\":{\"owner\":\"" + owner + "\"}}";

		long ownedTokensCount = 0;
		QueryResultsIterator<KeyValue> resultsIterator = stub.getQueryResult(query);
		while(resultsIterator.iterator().hasNext()) {
			resultsIterator.iterator().next();
			ownedTokensCount++;
		}

		return BigInteger.valueOf(ownedTokensCount);
	}

	public static String ownerOf(ChaincodeStub stub, BigInteger tokenId) throws Exception {
		NFT nft = NFT.read(stub, tokenId);
		return nft.getOwner();
	}

	public static boolean transferFrom(ChaincodeStub stub, String from, String to, BigInteger tokenId) throws Exception {
			NFT nft = NFT.read(stub, tokenId);

			String owner = nft.getOwner();
			if (!from.equals(owner)) {
				return false;
			}

			nft.setApprovee(stub, "");
			nft.setOwner(stub, to);
			return true;
	}

	public static boolean approve(ChaincodeStub stub, String approved, BigInteger tokenId) throws Exception {
		NFT nft = NFT.read(stub, tokenId);
		return nft.setApprovee(stub, approved);
	}

	public static boolean setApprovalForAll(ChaincodeStub stub, String caller, String operator, boolean approved) throws Exception {
		ObjectMapper mapper = new ObjectMapper();

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

    public static String getApproved(ChaincodeStub stub, BigInteger tokenId) throws Exception {
		NFT nft = NFT.read(stub, tokenId);
		return nft.getApprovee();
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
