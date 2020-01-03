package kr.ac.postech.sslab.type;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Signature extends EType implements IType {
    private String hash;

	private static final String HASH_KEY = "hash";

    @Override
    public void assign(List<String> args) {
        super.assign(args);
        this.hash = args.get(0);
    }

    @Override
    public void assign(Map<String, Object> map) {
        super.assign(map);
        this.hash = (String) map.get(HASH_KEY);
    }

    @Override
    public void setXAttr(String index, String value) {
        super.setXAttr(index, value);
    }

    @Override
    public String getXAttr(String index) {
        switch (index) {
            case HASH_KEY:
                return this.hash;

            default:
                return super.getXAttr(index);
        }
    }

    @Override
    public String toJSONString() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this.toMap());
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.putAll(super.toMap());
        map.put(HASH_KEY, this.hash);

        return map;
    }
}
