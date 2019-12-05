package kr.ac.postech.sslab.type;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

public class URI {
    private String path;
    private String hash;

    public URI() {
        this.path = "";
        this.hash = "";
    }

    public URI(String path, String hash) {
        this.path = path;
        this.hash = hash;
    }

    public String toJSONString() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this.toMap());
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("path", this.path);
        map.put("hash", this.hash);

        return map;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getHash() {
        return this.hash;
    }

    public void assign(Map<String, String> map) {
        this.path = map.get("path");
        this.hash = map.get("hash");
    }
}
