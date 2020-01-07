package com.poscoict.posledger.chaincode.assetscc.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.poscoict.posledger.chaincode.assetscc.constant.DataType.*;
import static com.poscoict.posledger.chaincode.assetscc.constant.DataType.LIST_BOOLEAN;
import static com.poscoict.posledger.chaincode.assetscc.constant.Message.NO_DATA_TYPE_MESSAGE;

public class DataTypeConversion {
    private static final Log LOG = LogFactory.getLog(DataTypeConversion.class);

    private DataTypeConversion() {}

    public static Object strToDataType(String dataType, String value) {
        switch (dataType) {
            case INTEGER:
                return Integer.parseInt(value);

            case BIG_INTEGER:
                return new BigInteger(value);

            case DOUBLE:
                return Double.parseDouble(value);

            case BYTE:
                return Byte.parseByte(value);

            case STRING:
                return value;

            case BOOLEAN:
                return Boolean.parseBoolean(value);

            case LIST_INTEGER:
                return toListInteger(value);

            case LIST_BIG_INTEGER:
                return toListBigInteger(value);

            case LIST_DOUBLE:
                return toListDouble(value);

            case LIST_BYTE:
                return toListByte(value);

            case LIST_STRING:
                return toListString(value);

            case LIST_BOOLEAN:
                return toListBoolean(value);

            default:
                LOG.error(NO_DATA_TYPE_MESSAGE);
                return null;
        }
    }

    private static List<Integer> toListInteger(String value) {
        List<Integer> integers = new ArrayList<>();
        if (value == null) {
            return integers;
        }

        List<String> strings = toList(value);
        for (String string : strings) {
            integers.add(Integer.parseInt(string));
        }

        return integers;
    }

    private static List<BigInteger> toListBigInteger(String value) {
        List<BigInteger> bigIntegers = new ArrayList<>();
        if (value == null) {
            return bigIntegers;
        }

        List<String> strings = toList(value);
        for (String string : strings) {
            bigIntegers.add(new BigInteger(string));
        }

        return bigIntegers;
    }

    private static List<Double> toListDouble(String value) {
        List<Double> doubles = new ArrayList<>();
        if (value == null) {
            return doubles;
        }

        List<String> strings = toList(value);
        for (String string : strings) {
            doubles.add(Double.parseDouble(string));
        }

        return doubles;
    }

    private static List<Byte> toListByte(String value) {
        List<Byte> bytes = new ArrayList<>();
        if (value == null) {
            return bytes;
        }

        List<String> strings = toList(value);
        for (String string : strings) {
            bytes.add(Byte.parseByte(string));
        }

        return bytes;
    }

    private static List<String> toListString(String value) {
        List<String> strings = new ArrayList<>();
        if (value == null) {
            return strings;
        }

        strings = toList(value);
        return strings;
    }

    private static List<Boolean> toListBoolean(String value) {
        List<Boolean> booleans = new ArrayList<>();
        if (value == null) {
            return booleans;
        }

        List<String> strings = toList(value);
        for (String string : strings) {
            booleans.add(Boolean.parseBoolean(string));
        }

        return booleans;
    }

    private static List<String> toList(String value) {
        return Arrays.asList(value.substring(1, value.length() - 1).trim().split(","));
    }
}
