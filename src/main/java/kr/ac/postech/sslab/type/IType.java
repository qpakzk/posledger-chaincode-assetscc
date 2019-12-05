package kr.ac.postech.sslab.type;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.Map;

public interface IType {
    void assign(ArrayList<String> args);
    void assign(Map<String, Object> map);
    void setXAttr(String index, String value);
    String getXAttr(String index);
    String toJSONString() throws JsonProcessingException;
    Map<String, Object> toMap();
}