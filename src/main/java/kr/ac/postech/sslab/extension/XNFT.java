package kr.ac.postech.sslab.extension;

import kr.ac.postech.sslab.nft.NFT;
import java.math.BigInteger;
import java.util.Map;

public class XNFT {
    public static boolean mint(BigInteger tokenId, String type, String owner, Map<String, Object> xattr, Map<String, String> uri) throws Exception {
        NFT nft = new NFT();
        boolean result = nft.mint(tokenId, type, owner, xattr, uri);
        return result;
    }

    public static boolean setURI(BigInteger tokenId, String index, String value) throws Exception {
        NFT nft = NFT.read(tokenId);
        nft.setURI(index, value);
        return true;
    }

    public static String getURI(BigInteger tokenId, String index) throws Exception {
        NFT nft = NFT.read(tokenId);
        String value = nft.getURI(index);
        return value;
    }

    public static boolean setXAttr(BigInteger tokenId, String index, Object value) throws Exception {
        NFT nft = NFT.read(tokenId);
        nft.setXAttr(index, value);
        return true;
    }

    public static Object getXAttr(BigInteger tokenId, String index) throws Exception {
        NFT nft = NFT.read(tokenId);
       Object value = nft.getXAttr(index);
       return value;
    }
}
