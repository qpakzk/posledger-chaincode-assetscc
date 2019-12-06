package kr.ac.postech.sslab.type;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Signature implements IType {
    private boolean activated;
    private String hash;

	private static final String ACTIVATED_KEY = "activated";
	private static final String HASH_KEY = "hash";

    @Override
    public void assign(List<String> args) {
        this.activated = true;
        this.hash = args.get(0);
    }

    @Override
    public void assign(Map<String, Object> map) {
        this.activated = (boolean) map.get(ACTIVATED_KEY);
        this.hash = (String) map.get(HASH_KEY);
    }

    @Override
    public void setXAttr(String index, String value) {
        if (index.equals(ACTIVATED_KEY)) {
            this.deactivate();
        }
    }

    @Override
    public String getXAttr(String index) {
        switch (index) {
            case ACTIVATED_KEY:
                return Boolean.toString(this.activated);

            case HASH_KEY:
                return this.hash;

            default:
                return null;
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
        map.put(ACTIVATED_KEY, this.activated);
        map.put(HASH_KEY, this.hash);

        return map;
    }

    private void deactivate() {
        this.activated = false;
    }
}
