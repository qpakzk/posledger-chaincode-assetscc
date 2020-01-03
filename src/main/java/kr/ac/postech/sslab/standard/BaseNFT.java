package kr.ac.postech.sslab.standard;

import kr.ac.postech.sslab.nft.NFT;
import java.math.BigInteger;

public class BaseNFT {
    public static boolean mint(BigInteger tokenId, String owner) throws Exception {
        NFT nft = new NFT();
        String type = "base";
        return nft.mint(tokenId, type, owner, null, null);
    }

    public static boolean burn(BigInteger tokenId) throws Exception {
        NFT nft = NFT.read(tokenId);
        return nft.burn(tokenId);
    }

    public static String getType(BigInteger tokenId) throws Exception {
        NFT nft = NFT.read(tokenId);
        return nft.getType();
    }
}
