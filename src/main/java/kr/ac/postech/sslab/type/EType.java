package kr.ac.postech.sslab.type;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class EType implements IType {
    private boolean activated;
    private String parent;
    private List<String> children;

    private static final String ACTIVATED_KEY = "activated";
    private static final String PARENT_KEY = "parent";
    private static final String CHILDREN_KEY = "children";

    @Override
    public void assign(List<String> args) {
        activated = true;
        parent = "";
        children = new ArrayList<>();
    }

    @Override
    public void assign(Map<String, Object> map) {
        this.activated = (boolean) map.get(ACTIVATED_KEY);
        this.parent = (String) map.get(PARENT_KEY);
        this.children = (ArrayList<String>) map.get(CHILDREN_KEY);
    }

    @Override
    public void setXAttr(String index, String value) {
        switch (index) {
            case ACTIVATED_KEY:
                this.deactivate();
                break;

            case PARENT_KEY:
                this.parent = value;
                break;

            case CHILDREN_KEY:
                this.children = this.toList(value);
                break;

            default:
                return;
        }
    }

    private void deactivate() {
        this.activated = false;
    }

    protected ArrayList<String> toList(String string) {
        return new ArrayList<>(Arrays.asList(string.split(",")));
    }

    @Override
    public String getXAttr(String index) {
        switch (index) {
            case ACTIVATED_KEY:
                return Boolean.toString(this.activated);

            case PARENT_KEY:
                return this.parent;

            case CHILDREN_KEY:
                return this.children.toString();

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
        map.put(PARENT_KEY, this.parent);
        map.put(CHILDREN_KEY, this.children);

        return map;
    }
}
