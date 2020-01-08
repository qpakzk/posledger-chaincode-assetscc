package com.poscoict.posledger.chaincode.assetscc.extension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.poscoict.posledger.chaincode.assetscc.constant.DataType;
import com.poscoict.posledger.chaincode.assetscc.constant.Message;
import com.poscoict.posledger.chaincode.assetscc.main.CustomChaincodeBase;
import com.poscoict.posledger.chaincode.assetscc.structure.NFT;
import com.poscoict.posledger.chaincode.assetscc.structure.TokenTypeManager;
import com.poscoict.posledger.chaincode.assetscc.util.DataTypeConversion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

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

        List<String> attr = TokenTypeManager.getTokenTypes().get(nft.getType()).get(index);
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

        List<String> attr = TokenTypeManager.getTokenTypes().get(nft.getType()).get(index);
        switch (attr.get(0)) {
            case DataType.INTEGER:
                return Integer.toString((int) value);

            case DataType.BIG_INTEGER:
                BigInteger bigInteger = (BigInteger) value;
                return bigInteger.toString();

            case DataType.DOUBLE:
                return Double.toString((double) value);

            case DataType.BYTE:
                return Byte.toString((byte) value);

            case DataType.STRING:
                return (String) value;

            case DataType.BOOLEAN:
                return Boolean.toString((boolean) value);

            case DataType.LIST_INTEGER:
                List<Integer> integers = (List<Integer>) value;
                return integers != null ? integers.toString() : null;

            case DataType.LIST_BIG_INTEGER:
                List<BigInteger> bigIntegers = (List<BigInteger>) value;
                return bigIntegers != null ? bigIntegers.toString() : null;

            case DataType.LIST_DOUBLE:
                List<Double> doubles = (List<Double>) value;
                return doubles != null ? doubles.toString() : null;

            case DataType.LIST_BYTE:
                List<Byte> bytes = (List<Byte>) value;
                return bytes != null ? bytes.toString() : null;

            case DataType.LIST_STRING:
                List<String> strings = (List<String>) value;
                return strings != null ? strings.toString() : null;

            case DataType.LIST_BOOLEAN:
                List<Boolean> booleans = (List<Boolean>) value;
                return booleans != null ? booleans.toString() : null;

            default:
                LOG.error(Message.NO_DATA_TYPE_MESSAGE);
                return null;
        }
    }
}
