package kr.ac.postech.sslab.main;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.postech.sslab.extension.*;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.math.BigInteger;
import java.util.*;

import static io.netty.util.internal.StringUtil.isNullOrEmpty;

public class CustomMain extends Main {
    private static final String ARG_MESSAGE = "Arguments must be exactly %d non-empty string(s)";
	private static ObjectMapper mapper = new ObjectMapper();

    @Override
    public Response invoke(ChaincodeStub stub) {
        try {
            CustomChainCodeStub.setChaincodeStub(stub);
            String func = stub.getFunction();
            List<String> args = stub.getParameters();
            String response;

            switch (func) {
                case "balanceOf": {
                    if (args.size() == 1) {
                        return super.invoke(stub);
                    }

                    if (args.size() != 2 || isNullOrEmpty(args.get(0))
                    || isNullOrEmpty(args.get(1))) {
                        throw new IllegalArgumentException(String.format(ARG_MESSAGE + " or %d", 1, 2));
                    }

                    String owner = args.get(0);
                    String type = args.get(1);
                    BigInteger balance = EERC721.balanceOf(owner, type);
                    response = balance.toString();
                    break;
                }

                case "tokenIdsOf": {
                    List<BigInteger> tokenIds;
                    if (args.size() == 1) {
                        if (isNullOrEmpty(args.get(0))) {
                            throw new IllegalArgumentException(String.format(ARG_MESSAGE, 1));
                        }

                        String owner = args.get(0);
                        tokenIds = EERC721.tokenIdsOf(owner);
                    }
                    else if (args.size() == 2) {
                        if (isNullOrEmpty(args.get(0)) || isNullOrEmpty(args.get(1))) {
                            throw new IllegalArgumentException(String.format(ARG_MESSAGE, 2));
                        }

                        String owner = args.get(0);
                        String type = args.get(1);
                        tokenIds = EERC721.tokenIdsOf(owner, type);
                    }
                    else {
                        throw new IllegalArgumentException(String.format(ARG_MESSAGE + " or %d", 1, 2));
                    }

                    response = tokenIds.toString();
                    break;
                }

                case "divide": {
                    if (args.size() != 4 || isNullOrEmpty(args.get(0))
                            || isNullOrEmpty(args.get(1)) || isNullOrEmpty(args.get(2)) || isNullOrEmpty(args.get(3))) {
                        throw new IllegalArgumentException(String.format(ARG_MESSAGE, 4));
                    }

                    BigInteger tokenId = new BigInteger(args.get(0));

                    List<String> newIdsStr = Arrays.asList(args.get(1)
                            .substring(1, args.get(1).length() - 1).split(", "));
                    List<BigInteger> newIds = new ArrayList<>();
                    for (String newIdStr : newIdsStr) {
                        BigInteger newId = new BigInteger(newIdStr);
                        newIds.add(newId);
                    }

                    List<Object> values = Arrays.asList(args.get(2)
                            .substring(1, args.get(2).length() - 1).split(", "));

                    String index = args.get(3);
                    boolean result = EERC721.divide(tokenId, newIds, values, index);
                    response = Boolean.toString(result);
                    break;
                }

                case "deactivate": {
                    if (args.size() != 1 || isNullOrEmpty(args.get(0))) {
                        throw new IllegalArgumentException(String.format(ARG_MESSAGE, 1));
                    }

                    BigInteger tokenId = new BigInteger(args.get(0));
                    boolean result = EERC721.deactivate(tokenId);
                    response = Boolean.toString(result);
                    break;
                }

                case "query": {
                    if (args.size() != 1 || isNullOrEmpty(args.get(0))) {
                        throw new IllegalArgumentException(String.format(ARG_MESSAGE, 1));
                    }

                    BigInteger tokenId = new BigInteger(args.get(0));
                    String query = EERC721.query(tokenId);
                    response = query;
                    break;
                }

                case "queryHistory": {
                    if (args.size() != 1 || isNullOrEmpty(args.get(0))) {
                        throw new IllegalArgumentException(String.format(ARG_MESSAGE, 1));
                    }

                    BigInteger tokenId = new BigInteger(args.get(0));
                    List<String> histories = EERC721.queryHistory(tokenId);
                    response = histories.toString();
                    break;
                }

                case "mint": {
                    if (args.size() == 2) {
                        return super.invoke(stub);
                    }

                    if (args.size() != 5 || isNullOrEmpty(args.get(0))
                            || isNullOrEmpty(args.get(1)) || isNullOrEmpty(args.get(2))
                            || isNullOrEmpty(args.get(3)) || isNullOrEmpty(args.get(4))) {
                        throw new IllegalArgumentException(String.format(ARG_MESSAGE + " or %d", 2, 5));
                    }

                    BigInteger tokenId = new BigInteger(args.get(0));
                    String type = args.get(1);
                    String owner = args.get(2);
                    Map<String, Object> xattr =
                            mapper.readValue(args.get(3), new TypeReference<HashMap<String, Object>>(){});
                    Map<String, String> uri =
                            mapper.readValue(args.get(4), new TypeReference<HashMap<String, String>>(){});
                    boolean result = XNFT.mint(tokenId, type, owner, xattr, uri);
                    response = Boolean.toString(result);
                    break;
                }

                case "setURI": {
                    if (args.size() != 3 || isNullOrEmpty(args.get(0))
                            || isNullOrEmpty(args.get(1)) || isNullOrEmpty(args.get(2))) {
                        throw new IllegalArgumentException(String.format(ARG_MESSAGE, 3));
                    }
                    BigInteger tokenId = new BigInteger(args.get(0));
                    String index = args.get(1);
                    String value = args.get(2);
                    boolean result = XNFT.setURI(tokenId, index, value);
                    response = Boolean.toString(result);
                    break;
                }

                case  "getURI": {
                    if (args.size() != 2 || isNullOrEmpty(args.get(0))
                            || isNullOrEmpty(args.get(1))) {
                        throw new IllegalArgumentException(String.format(ARG_MESSAGE, 2));
                    }
                    BigInteger tokenId = new BigInteger(args.get(0));
                    String index = args.get(1);
                    String value = XNFT.getURI(tokenId, index);
                    response = value;
                    break;
                }

                case "setXAttr": {
                    if (args.size() != 3 || isNullOrEmpty(args.get(0))
                            || isNullOrEmpty(args.get(1)) || isNullOrEmpty(args.get(2))) {
                        throw new IllegalArgumentException(String.format(ARG_MESSAGE, 3));
                    }
                    BigInteger tokenId = new BigInteger(args.get(0));
                    String index = args.get(1);
                    Object value = args.get(2);
                    boolean result = XNFT.setXAttr(tokenId, index, value);
                    response = Boolean.toString(result);
                    break;
                }

                case "getXAttr": {
                    if (args.size() != 2 || isNullOrEmpty(args.get(0))
                            || isNullOrEmpty(args.get(1))) {
                        throw new IllegalArgumentException(String.format(ARG_MESSAGE, 2));
                    }
                    BigInteger tokenId = new BigInteger(args.get(0));
                    String index = args.get(1);
                    Object value = XNFT.getXAttr(tokenId, index);
                    response = (String) value;
                    break;
                }
                default:
                    return super.invoke(stub);
            }

            return newSuccessResponse(response);
        } catch (Exception e) {
            return newErrorResponse(e.getMessage());
        }
    }

    public static void main(String[] args) {
        new CustomMain().start(args);
    }
}
