package kr.ac.postech.sslab.main;

import kr.ac.postech.sslab.standard.BaseNFT;
import kr.ac.postech.sslab.standard.ERC721;
import org.hyperledger.fabric.shim.ChaincodeStub;
import static io.netty.util.internal.StringUtil.isNullOrEmpty;

import java.math.BigInteger;
import java.util.*;

public class Main extends CustomChaincodeBase {
    private static final String ARG_MESSAGE = "Arguments must be exactly %d non-empty string(s)";
    private static final String NO_FUNCTION_MESSAGE = "There is no such function";

    @Override
    public Response invoke(ChaincodeStub stub) {
        try {
            String func = stub.getFunction();
            List<String> args = stub.getParameters();
            String response;

            switch (func) {
                case "balanceOf": {
                    if (args.size() != 1 || isNullOrEmpty(args.get(0))) {
                        throw new IllegalArgumentException(String.format(ARG_MESSAGE, 1));
                    }

                    String owner = args.get(0);
                    BigInteger balance = ERC721.balanceOf(stub, owner);
                    response = balance.toString();
                    break;
                }

                case "ownerOf": {
                    if (args.size() != 1 || isNullOrEmpty(args.get(0))) {
                        throw new IllegalArgumentException(String.format(ARG_MESSAGE, 1));
                    }

                    BigInteger tokenId = new BigInteger(args.get(0));

                    String owner = ERC721.ownerOf(stub, tokenId);
                    response = owner;
                    break;
                }

                case "transferFrom": {
                    if (args.size() != 3 || isNullOrEmpty(args.get(0))
                            || isNullOrEmpty(args.get(1)) || isNullOrEmpty(args.get(2))) {
                        throw new IllegalArgumentException(String.format(ARG_MESSAGE, 3));
                    }

                    String from = args.get(0);
                    String to = args.get(1);
                    BigInteger tokenId = new BigInteger(args.get(2));

                    boolean result = ERC721.transferFrom(stub, from, to, tokenId);
                    response = Boolean.toString(result);
                    break;
                }

                case "approve": {
                    if (args.size() != 2 || isNullOrEmpty(args.get(0))
                            || isNullOrEmpty(args.get(1))) {
                        throw new IllegalArgumentException(String.format(ARG_MESSAGE, 2));
                    }

                    String approved = args.get(0);
                    BigInteger tokenId = new BigInteger(args.get(1));

                    boolean result = ERC721.approve(stub, approved, tokenId);
                    response = Boolean.toString(result);
                    break;
                }

                case "setApprovalForAll": {
                    if (args.size() != 3 || isNullOrEmpty(args.get(0))
                            || isNullOrEmpty(args.get(1)) || isNullOrEmpty(args.get(2))) {
                        throw new IllegalArgumentException(String.format(ARG_MESSAGE, 3));
                    }

                    String caller = args.get(0);
                    String operator = args.get(1);
                    Boolean approved = Boolean.parseBoolean(args.get(2));

                    boolean result = ERC721.setApprovalForAll(stub, caller, operator, approved);
                    response = Boolean.toString(result);
                    break;
                }

                case "getApproved": {
                    if (args.size() != 1 || isNullOrEmpty(args.get(0))) {
                        throw new IllegalArgumentException(String.format(ARG_MESSAGE, 1));
                    }

                    BigInteger tokenId = new BigInteger(args.get(0));

                    String approved = ERC721.getApproved(stub, tokenId);
                    response = approved;
                    break;
                }

                case "isApprovedForAll": {
                    if (args.size() != 2 || isNullOrEmpty(args.get(0))
                            || isNullOrEmpty(args.get(1))) {
                        throw new IllegalArgumentException(String.format(ARG_MESSAGE, 2));
                    }

                    String owner = args.get(0);
                    String operator = args.get(1);

                    boolean result = ERC721.isApprovedForAll(owner, operator);
                    response = Boolean.toString(result);
                    break;
                }

                case "mint": {
                    if (args.size() != 2 || isNullOrEmpty(args.get(0))
                            || isNullOrEmpty(args.get(1))) {
                        throw new IllegalArgumentException(String.format(ARG_MESSAGE, 2));
                    }

                    BigInteger tokenId = new BigInteger(args.get(0));
                    String owner = args.get(1);

                    boolean result = BaseNFT.mint(stub, tokenId, owner);
                    response = Boolean.toString(result);
                    break;
                }

                case "burn": {
                    if (args.size() != 1 || isNullOrEmpty(args.get(0))) {
                        throw new IllegalArgumentException(String.format(ARG_MESSAGE, 1));
                    }

                    BigInteger tokenId = new BigInteger(args.get(0));

                    boolean result = BaseNFT.burn(stub, tokenId);
                    response = Boolean.toString(result);
                    break;
                }

                case "getType": {
                    if (args.size() != 1 || isNullOrEmpty(args.get(0))) {
                        throw new IllegalArgumentException(String.format(ARG_MESSAGE, 1));
                    }

                    BigInteger tokenId = new BigInteger(args.get(0));

                    String type = BaseNFT.getType(stub, tokenId);
                    response = type;
                    break;
                }

                default:
                    throw new Exception(NO_FUNCTION_MESSAGE);
            }

            return newSuccessResponse(response);
        } catch (Exception e) {
            return newErrorResponse(e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Main().start(args);
    }

}
