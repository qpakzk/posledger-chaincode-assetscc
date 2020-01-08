package com.poscoict.posledger.chaincode.assetscc.constant;

public final class Function {
    private Function() {}

    public static final String BALANCE_OF_FUNCTION_NAME = "balanceOf";
    public static final String OWNER_OF_FUNCTION_NAME = "ownerOf";
    public static final String TRANSFER_FROM_FUNCTION_NAME = "transferFrom";
    public static final String APPROVE_FUNCTION_NAME = "approve";
    public static final String SET_APPROVAL_FOR_ALL_FUNCTION_NAME = "setApprovalForAll";
    public static final String GET_APPROVED_FUNCTION_NAME = "getApproved";
    public static final String IS_APPROVED_FOR_ALL_FUNCTION_NAME = "isApprovedForAll";

    public static final String MINT_FUNCTION_NAME = "mint";
    public static final String BURN_FUNCTION_NAME = "burn";
    public static final String GET_TYPE_FUNCTION_NAME = "getType";

    public static final String TOKEN_IDS_OF_FUNCTION_NAME = "tokenIdsOf";
    public static final String DIVIDE_FUNCTION_NAME = "divide";
    public static final String DEACTIVATE_FUNCTION_NAME = "deactivate";
    public static final String QUERY_FUNCTION_NAME = "query";
    public static final String QUERY_HISTORY_FUNCTION_NAME = "queryHistory";

    public static final String SET_URI_FUNCTION_NAME = "setURI";
    public static final String GET_URI_FUNCTION_NAME = "getURI";
    public static final String SET_XATTR_FUNCTION_NAME = "setXAttr";
    public static final String GET_XATTR_FUNCTION_NAME = "getXAttr";

    public static final String ENROLL_TOKEN_TYPE_FUNCTION_NAME = "enrollTokenType";
    public static final String DROP_TOKEN_TYPE_FUNCTION_NAME = "dropTokenType";
    public static final String TOKEN_TYPES_OF_FUNCTION_NAME = "tokenTypesOf";
    public static final String UPDATE_TOKEN_TYPE_FUNCTION_NAME = "updateTokenType";
    public static final String RETRIEVE_TOKEN_TYPE_FUNCTION_NAME = "retrieveTokenType";
    public static final String EMROLL_ATTRIBUTE_OF_TOKEN_TYPE_FUNCTION_NAME = "enrollAttributeOfTokenType";
    public static final String DROP_ATTRIBUTE_OF_TOKEN_TYPE_FUNCTION_NAME = "dropAttributeOfTokenType";
    public static final String UPDATE_ATTRIBUTE_OF_TOKEN_TYPE_FUNCTION_NAME = "updateAttributeOfTokenType";
    public static final String RETRIEVE_ATTRIBUTE_OF_TOKEN_TYPE_FUNCTION_NAME = "retrieveAttributeOfTokenType";
}
