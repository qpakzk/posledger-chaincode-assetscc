package kr.ac.postech.sslab.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Map;

public interface IXAttr {
    void setXAttr(String index, String value);
    String getXAttr(String index);
    String toJSONString() throws JsonProcessingException;
    Map<String, Object> toMap();
}