package kr.ac.postech.sslab.extension;

import kr.ac.postech.sslab.main.CustomChaincodeBase;
import kr.ac.postech.sslab.nft.NFT;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.math.BigInteger;
import java.util.Map;

public class XNFT extends CustomChaincodeBase {
    public static boolean mint(ChaincodeStub stub, BigInteger tokenId, String type, String owner, Map<String, Object> xattr, Map<String, String> uri) throws Exception {
        NFT nft = new NFT();
        boolean result = nft.mint(stub, tokenId, type, owner, xattr, uri);
        return result;
    }

    public static boolean setURI(ChaincodeStub stub, BigInteger tokenId, String index, String value) throws Exception {
        NFT nft = NFT.read(stub, tokenId);
        nft.setURI(stub, index, value);
        return true;
    }

    public static String getURI(ChaincodeStub stub, BigInteger tokenId, String index) throws Exception {
        NFT nft = NFT.read(stub, tokenId);
        String value = nft.getURI(index);
        return value;
    }

    public static boolean setXAttr(ChaincodeStub stub, BigInteger tokenId, String index, Object value) throws Exception {
        NFT nft = NFT.read(stub, tokenId);
        nft.setXAttr(stub, index, value);
        return true;
    }

    public static Object getXAttr(ChaincodeStub stub, BigInteger tokenId, String index) throws Exception {
        NFT nft = NFT.read(stub, tokenId);
       Object value = nft.getXAttr(index);
       return value;
    }
}
