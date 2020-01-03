package kr.ac.postech.sslab.standard;

import kr.ac.postech.sslab.nft.NFT;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.math.BigInteger;

public class BaseNFT {
    public static boolean mint(ChaincodeStub stub, BigInteger tokenId, String owner) throws Exception {
        NFT nft = new NFT();
        String type = "base";
        return nft.mint(stub, tokenId, type, owner, null, null);
    }

    public static boolean burn(ChaincodeStub stub, BigInteger tokenId) throws Exception {
        NFT nft = NFT.read(stub, tokenId);
        return nft.burn(stub, tokenId);
    }

    public static String getType(ChaincodeStub stub, BigInteger tokenId) throws Exception {
        NFT nft = NFT.read(stub, tokenId);
        return nft.getType();
    }
}
