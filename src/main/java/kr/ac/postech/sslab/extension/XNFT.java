package kr.ac.postech.sslab.extension;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.ac.postech.sslab.main.CustomChaincodeBase;
import kr.ac.postech.sslab.structure.NFT;
import kr.ac.postech.sslab.structure.TokenTypes;
import kr.ac.postech.sslab.util.DataTypeConversion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static kr.ac.postech.sslab.constant.DataType.*;

public class XNFT extends CustomChaincodeBase {
    private static final Log LOG = LogFactory.getLog(XNFT.class);

    public static boolean mint(ChaincodeStub stub, BigInteger tokenId, String type, String owner, Map<String, Object> xattr, Map<String, String> uri) throws JsonProcessingException {
        NFT nft = new NFT();
        boolean check1 = XType.initXAttr(type, xattr);
        LOG.info("XNFT::mint:: XType.initXAttr returns " + check1);
        boolean check2 = XType.checkURI(uri);
        LOG.info("XNFT::mint:: XType.checkURI returns " + check2);
        if (!(check1 && check2)) {
            return false;
        }

        XType.addXAttrForEERC721(xattr);

        return nft.mint(stub, tokenId, type, owner, xattr, uri);
    }

    public static boolean setURI(ChaincodeStub stub, BigInteger tokenId, String index, String value) throws IOException {
        NFT nft = NFT.read(stub, tokenId);
        Map<String, String> uri = nft.getURI();
        if (!uri.containsKey(index)) {
            return false;
        }

        return nft.setURI(stub, index, value);
    }

    public static String getURI(ChaincodeStub stub, BigInteger tokenId, String index) throws IOException {
        NFT nft = NFT.read(stub, tokenId);
        Map<String, String> uri = nft.getURI();
        if (!uri.containsKey(index)) {
            return null;
        }

        return nft.getURI(index);
    }

    public static boolean setXAttr(ChaincodeStub stub, BigInteger tokenId, String index, String value) throws IOException {
        NFT nft = NFT.read(stub, tokenId);
        Map<String, Object> xattr = nft.getXAttr();
        if (!xattr.containsKey(index)) {
            return false;
        }

        List<String> attr = TokenTypes.getTokenTypes().get(nft.getType()).get(index);
        Object object = DataTypeConversion.strToDataType(attr.get(0), value);
        if (object == null) {
            return false;
        }

        nft.setXAttr(stub, index, object);

        return true;
    }

    @SuppressWarnings("unchecked")
    public static String getXAttr(ChaincodeStub stub, BigInteger tokenId, String index) throws IOException {
        NFT nft = NFT.read(stub, tokenId);
        Map<String, Object> xattr = nft.getXAttr();
        if (!xattr.containsKey(index)) {
            return null;
        }

       Object value = nft.getXAttr(index);

        List<String> attr = TokenTypes.getTokenTypes().get(nft.getType()).get(index);
        switch (attr.get(0)) {
            case INTEGER:
                return Integer.toString((int) value);

            case BIG_INTEGER: {
                BigInteger bigInt = (BigInteger) value;
                return bigInt.toString();
            }
            case DOUBLE:
                return Double.toString((double) value);

            case BYTE:
                return Byte.toString((byte) value);

            case STRING:
                return (String) value;

            case BOOLEAN:
                return Boolean.toString((boolean) value);

            case LIST_INTEGER: {
                List<Integer> list = (List<Integer>) value;
                return list != null ? list.toString() : null;
            }

            case LIST_BIG_INTEGER: {
                List<BigInteger> list = (List<BigInteger>) value;
                return list != null ? list.toString() : null;
            }

            case LIST_DOUBLE: {
                List<Double> list = (List<Double>) value;
                return list != null ? list.toString() : null;
            }

            case LIST_BYTE: {
                List<Byte> list = (List<Byte>) value;
                return list != null ? list.toString() : null;
            }

            case LIST_STRING: {
                List<String> list = (List<String>) value;
                return list != null ? list.toString() : null;
            }

            case LIST_BOOLEAN: {
                List<Boolean> list = (List<Boolean>) value;
                return list != null ? list.toString() : null;
            }
            default:
                return null;
        }
    }

    private static List<String> toList(String value) {
        return Arrays.asList(value.substring(1, value.length() - 1).split(", "));
    }
}
