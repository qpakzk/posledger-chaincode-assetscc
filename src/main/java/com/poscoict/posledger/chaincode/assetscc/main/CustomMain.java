package com.poscoict.posledger.chaincode.assetscc.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poscoict.posledger.chaincode.assetscc.constant.Function;
import com.poscoict.posledger.chaincode.assetscc.extension.EERC721;
import com.poscoict.posledger.chaincode.assetscc.extension.XNFT;
import com.poscoict.posledger.chaincode.assetscc.extension.XType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import static com.poscoict.posledger.chaincode.assetscc.constant.Message.ARG_MESSAGE;
import static io.netty.util.internal.StringUtil.isNullOrEmpty;

public class CustomMain extends Main {
    private static final Log LOG = LogFactory.getLog(CustomMain.class);

	private static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Response invoke(ChaincodeStub stub) {
        try {
            String func = stub.getFunction();
            List<String> args = stub.getParameters();
            String response;

            switch (func) {
                case Function.BALANCE_OF_FUNCTION_NAME:
                    if (args.size() == 1) {
                        return super.invoke(stub);
                    }
                    response = xBalanceOf(stub, args);
                    break;

                case Function.TOKEN_IDS_OF_FUNCTION_NAME:
                    response = tokenIdsOf(stub, args);
                    break;

                case Function.DIVIDE_FUNCTION_NAME:
                    response = divide(stub, args);
                    break;

                case Function.DEACTIVATE_FUNCTION_NAME:
                    response = deactivate(stub, args);
                    break;

                case Function.QUERY_FUNCTION_NAME:
                    response = query(stub, args);
                    break;

                case Function.QUERY_HISTORY_FUNCTION_NAME:
                    response = queryHistory(stub, args);
                    break;

                case Function.MINT_FUNCTION_NAME:
                    if (args.size() == 2) {
                        return super.invoke(stub);
                    }
                    response = xMint(stub, args);
                    break;

                case Function.SET_URI_FUNCTION_NAME:
                    response = setURI(stub, args);
                    break;

                case  Function.GET_URI_FUNCTION_NAME:
                    response = getURI(stub, args);
                    break;

                case Function.SET_XATTR_FUNCTION_NAME:
                    response = setXAttr(stub, args);
                    break;

                case Function.GET_XATTR_FUNCTION_NAME:
                    response = getXAttr(stub, args);
                    break;

                case Function.REGISTER_TOKEN_TYPE_FUNCTION_NAME:
                    response = registerTokenType(stub, args);
                    break;

                case Function.TOKEN_TYPES_OF_FUNCTION:
                    response = tokenTypesOf();
                    break;

                case Function.GET_TOKEN_TYPE_FUNCTION_NAME:
                    response = getTokenType(args);
                    break;

                default:
                    return super.invoke(stub);
            }

            return newSuccessResponse(response);
        } catch (Exception e) {
            LOG.error("CustomMain::invoke:: invoke error");
            return newErrorResponse(e.getMessage());
        }
    }

    private String xBalanceOf(ChaincodeStub stub, List<String> args) throws IOException {
        if (args.size() != 2 || isNullOrEmpty(args.get(0))
                || isNullOrEmpty(args.get(1))) {
            throw new IllegalArgumentException(String.format(ARG_MESSAGE + "1 or 2"));
        }

        String owner = args.get(0);
        String type = args.get(1);

        return EERC721.balanceOf(stub, owner, type).toString();
    }

    private String tokenIdsOf(ChaincodeStub stub, List<String> args) throws IOException {
        List<BigInteger> tokenIds;

        if (args.size() == 1) {
            if (isNullOrEmpty(args.get(0))) {
                throw new IllegalArgumentException(String.format(ARG_MESSAGE, "1"));
            }

            String owner = args.get(0);
            tokenIds = EERC721.tokenIdsOf(stub, owner);
        }
        else if (args.size() == 2) {
            if (isNullOrEmpty(args.get(0)) || isNullOrEmpty(args.get(1))) {
                throw new IllegalArgumentException(String.format(ARG_MESSAGE, "2"));
            }

            String owner = args.get(0);
            String type = args.get(1);
            tokenIds = EERC721.tokenIdsOf(stub, owner, type);
        }
        else {
            throw new IllegalArgumentException(String.format(ARG_MESSAGE + "1 or 2"));
        }

        return tokenIds.toString();
    }

    private String divide(ChaincodeStub stub, List<String> args) throws IOException {
        if (args.size() != 4
                || isNullOrEmpty(args.get(0))
                || isNullOrEmpty(args.get(1))
                || isNullOrEmpty(args.get(2))
                || isNullOrEmpty(args.get(3))) {
            throw new IllegalArgumentException(String.format(ARG_MESSAGE, "4"));
        }

        BigInteger tokenId = new BigInteger(args.get(0));

        List<String> newIdsStr = strToList(args.get(1));
        List<BigInteger> newIds = new ArrayList<>();
        for (String newIdStr : newIdsStr) {
            BigInteger newId = new BigInteger(newIdStr);
            newIds.add(newId);
        }

        List<String> values = strToList(args.get(2));

        String index = args.get(3);

        return Boolean.toString(EERC721.divide(stub, tokenId, newIds, values, index));
    }

    private String deactivate(ChaincodeStub stub, List<String> args) throws IOException {
        if (args.size() != 1 || isNullOrEmpty(args.get(0))) {
            throw new IllegalArgumentException(String.format(ARG_MESSAGE, "1"));
        }

        BigInteger tokenId = new BigInteger(args.get(0));

        return Boolean.toString(EERC721.deactivate(stub, tokenId));
    }

    private String query(ChaincodeStub stub, List<String> args) throws IOException {
        if (args.size() != 1 || isNullOrEmpty(args.get(0))) {
            throw new IllegalArgumentException(String.format(ARG_MESSAGE, "1"));
        }

        BigInteger tokenId = new BigInteger(args.get(0));

        return EERC721.query(stub, tokenId);
    }

    private String queryHistory(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1 || isNullOrEmpty(args.get(0))) {
            throw new IllegalArgumentException(String.format(ARG_MESSAGE, "1"));
        }

        BigInteger tokenId = new BigInteger(args.get(0));

        return EERC721.queryHistory(stub, tokenId).toString();
    }

    private String xMint(ChaincodeStub stub, List<String> args) throws IOException {
        if (args.size() != 5 || isNullOrEmpty(args.get(0))
                || isNullOrEmpty(args.get(1)) || isNullOrEmpty(args.get(2))
                || isNullOrEmpty(args.get(3)) || isNullOrEmpty(args.get(4))) {
            throw new IllegalArgumentException(String.format(ARG_MESSAGE, "5"));
        }

        BigInteger tokenId = new BigInteger(args.get(0));
        String type = args.get(1);
        String owner = args.get(2);
        Map<String, Object> xattr =
                objectMapper.readValue(args.get(3), new TypeReference<HashMap<String, Object>>(){});
        Map<String, String> uri =
                objectMapper.readValue(args.get(4), new TypeReference<HashMap<String, String>>(){});

        return Boolean.toString(XNFT.mint(stub, tokenId, type, owner, xattr, uri));
    }

    private String setURI(ChaincodeStub stub, List<String> args) throws IOException {
        if (args.size() != 3 || isNullOrEmpty(args.get(0))
                || isNullOrEmpty(args.get(1)) || isNullOrEmpty(args.get(2))) {
            throw new IllegalArgumentException(String.format(ARG_MESSAGE, "3"));
        }

        BigInteger tokenId = new BigInteger(args.get(0));
        String index = args.get(1);
        String value = args.get(2);

        return Boolean.toString(XNFT.setURI(stub, tokenId, index, value));
    }

    private String getURI(ChaincodeStub stub, List<String> args) throws IOException {
        if (args.size() != 2 || isNullOrEmpty(args.get(0))
                || isNullOrEmpty(args.get(1))) {
            throw new IllegalArgumentException(String.format(ARG_MESSAGE, "2"));
        }
        BigInteger tokenId = new BigInteger(args.get(0));
        String index = args.get(1);
        return XNFT.getURI(stub, tokenId, index);
    }

    private String setXAttr(ChaincodeStub stub, List<String> args) throws IOException {
        if (args.size() != 3 || isNullOrEmpty(args.get(0))
                || isNullOrEmpty(args.get(1)) || isNullOrEmpty(args.get(2))) {
            throw new IllegalArgumentException(String.format(ARG_MESSAGE, "3"));
        }

        BigInteger tokenId = new BigInteger(args.get(0));
        String index = args.get(1);
        String value = args.get(2);

        return Boolean.toString(XNFT.setXAttr(stub, tokenId, index, value));
    }

    private String getXAttr(ChaincodeStub stub, List<String> args)  throws IOException {
        if (args.size() != 2 || isNullOrEmpty(args.get(0))
                || isNullOrEmpty(args.get(1))) {
            throw new IllegalArgumentException(String.format(ARG_MESSAGE, "2"));
        }

        BigInteger tokenId = new BigInteger(args.get(0));
        String index = args.get(1);

        return XNFT.getXAttr(stub, tokenId, index);
    }

    private String registerTokenType(ChaincodeStub stub, List<String> args) throws IOException {
        if (args.size() != 2 || isNullOrEmpty(args.get(0))
                || isNullOrEmpty(args.get(1))) {
            throw new IllegalArgumentException(String.format(ARG_MESSAGE, "2"));
        }

        String type = args.get(0);
        String json = args.get(1);

        return Boolean.toString(XType.registerTokenType(stub, type, json));
    }

    private String tokenTypesOf() {
        List<String> tokenTypes = XType.tokenTypesOf();
        return tokenTypes.toString();
    }

    private String getTokenType(List<String> args) throws JsonProcessingException {
        if (args.size() != 1 || isNullOrEmpty(args.get(0))) {
            throw new IllegalArgumentException(String.format(ARG_MESSAGE, "1"));
        }

        String type = args.get(0);
        Map<String, List<String>> map = XType.getTokenType(type);

        return objectMapper.writeValueAsString(map);
    }

    private List<String> strToList(String str) {
        return Arrays.asList(str.substring(1, str.length() - 1).split(", "));
    }

    public static void main(String[] args) {
        new CustomMain().start(args);
    }
}
