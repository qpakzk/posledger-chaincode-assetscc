package kr.ac.postech.sslab.extension;

import javafx.util.Pair;
import kr.ac.postech.sslab.main.CustomChaincodeBase;
import kr.ac.postech.sslab.nft.NFT;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class XNFT extends CustomChaincodeBase {
    private static final String INTEGER = "Integer";
    private static final String BIG_INTEGER = "BigInteger";
    private static final String DOUBLE = "Double";
    private static final String BYTE = "Byte";
    private static final String STRING = "String";
    private static final String BOOLEAN = "Boolean";
    private static final String LIST_INTEGER = "[Integer]";
    private static final String LIST_BIG_INTEGER = "[BigInteger]";
    private static final String LIST_DOUBLE = "[Double]";
    private static final String LIST_BYTE = "[Byte]";
    private static final String LIST_STRING = "[String]";
    private static final String LIST_BOOLEAN = "[Boolean]";


    public static boolean mint(ChaincodeStub stub, BigInteger tokenId, String type, String owner, Map<String, Object> xattr, Map<String, String> uri) throws Exception {
        NFT nft = new NFT();
        boolean check1 = XType.initXAttr(type, xattr);
        boolean check2 = XType.checkURI(uri);
        if (!(check1 && check2)) {
            return false;
        }

        boolean result = nft.mint(stub, tokenId, type, owner, xattr, uri);
        return result;
    }

    public static boolean setURI(ChaincodeStub stub, BigInteger tokenId, String index, String value) throws Exception {
        NFT nft = NFT.read(stub, tokenId);
        Map<String, String> uri = nft.getURI();
        if (!uri.containsKey(index)) {
            return false;
        }

        nft.setURI(stub, index, value);
        return true;
    }

    public static String getURI(ChaincodeStub stub, BigInteger tokenId, String index) throws Exception {
        NFT nft = NFT.read(stub, tokenId);
        Map<String, String> uri = nft.getURI();
        if (!uri.containsKey(index)) {
            return null;
        }

        String value = nft.getURI(index);
        return value;
    }

    public static boolean setXAttr(ChaincodeStub stub, BigInteger tokenId, String index, String value) throws Exception {
        NFT nft = NFT.read(stub, tokenId);
        Map<String, Object> xattr = nft.getXAttr();
        if (!xattr.containsKey(index)) {
            return false;
        }

        Pair<String, Object> attr = tokenTypes.get(nft.getType()).get(index);
        switch (attr.getKey()) {
            case INTEGER:
                nft.setXAttr(stub, index, Integer.parseInt(value));
                break;

            case BIG_INTEGER:
                nft.setXAttr(stub, index, new BigInteger(value));
                break;

            case DOUBLE:
                nft.setXAttr(stub, index, Double.parseDouble(value));
                break;

            case BYTE:
                nft.setXAttr(stub, index, Byte.parseByte(value));
                break;

            case STRING:
                nft.setXAttr(stub, index, value);
                break;

            case BOOLEAN:
                nft.setXAttr(stub, index, Boolean.parseBoolean(value));
                break;

            case LIST_INTEGER: {
                List<String> values1 = toList(value);
                        List<Integer> values2 = new ArrayList<>();
                for (String value1 : values1) {
                    Integer value2 = Integer.parseInt(value1);
                    values2.add(value2);
                }

                nft.setXAttr(stub, index, values2);
                break;
            }

            case LIST_BIG_INTEGER: {
                List<String> values1 = toList(value);
                List<BigInteger> values2 = new ArrayList<>();
                for (String value1 : values1) {
                    BigInteger value2 = new BigInteger(value1);
                    values2.add(value2);
                }

                nft.setXAttr(stub, index, values2);
                break;
            }

            case LIST_DOUBLE: {
                List<String> values1 = toList(value);
                List<Double> values2 = new ArrayList<>();
                for (String value1 : values1) {
                    Double value2 = Double.parseDouble(value1);
                    values2.add(value2);
                }

                nft.setXAttr(stub, index, values2);
                break;
            }

            case LIST_BYTE: {
                List<String> values1 = toList(value);
                List<Byte> values2 = new ArrayList<>();
                for (String value1 : values1) {
                    Byte value2 = Byte.parseByte(value1);
                    values2.add(value2);
                }

                nft.setXAttr(stub, index, values2);
                break;
            }

            case LIST_STRING: {
                List<String> values = toList(value);
                nft.setXAttr(stub, index, values);
                break;
            }

            case LIST_BOOLEAN: {
                List<String> values1 = toList(value);
                List<Boolean> values2 = new ArrayList<>();
                for (String value1 : values1) {
                    Boolean value2 = Boolean.parseBoolean(value1);
                    values2.add(value2);
                }

                nft.setXAttr(stub, index, values2);
                break;
            }

            default:
                return false;
        }

        return true;
    }

    public static String getXAttr(ChaincodeStub stub, BigInteger tokenId, String index) throws Exception {
        NFT nft = NFT.read(stub, tokenId);
        Map<String, Object> xattr = nft.getXAttr();
        if (!xattr.containsKey(index)) {
            return null;
        }

       Object value = nft.getXAttr(index);

        Pair<String, Object> attr = tokenTypes.get(nft.getType()).get(index);
        switch (attr.getKey()) {
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
                return list.toString();
            }

            case LIST_BIG_INTEGER: {
                List<BigInteger> list = (List<BigInteger>) value;
                return list.toString();
            }

            case LIST_DOUBLE: {
                List<Double> list = (List<Double>) value;
                return list.toString();
            }

            case LIST_BYTE: {
                List<Byte> list = (List<Byte>) value;
                return list.toString();
            }

            case LIST_STRING: {
                List<String> list = (List<String>) value;
                return list.toString();
            }

            case LIST_BOOLEAN: {
                List<Boolean> list = (List<Boolean>) value;
                return list.toString();
            }
            default:
                return null;
        }
    }

    private static List<String> toList(String value) {
        return Arrays.asList(value.substring(1, value.length() - 1).split(", "));
    }
}
