package kr.ac.postech.sslab.type;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Document implements IType {
    private boolean activated;
    private String parent;
    private ArrayList<String> children;
    private int pages;

    private String hash;
    private ArrayList<String> signers;
    private ArrayList<String> sigIds;

	private static final String ACTIVATED_KEY = "activated";
	private static final String PARENT_KEY = "parent";
	private static final String CHILDREN_KEY = "children";
	private static final String PAGES_KEY = "pages";
	private static final String HASH_KEY = "hash";
	private static final String SIGNERS_KEY = "signers";
	private static final String SIGIDS_KEY = "sigIds";

    @Override
    public void assign(List<String> args) {
        this.activated = true;
        this.parent = "";
        this.children = new ArrayList<>();
        this.pages = Integer.parseInt(args.get(0));
        this.hash = args.get(1);
        this.signers = this.toList(args.get(2));
        this.sigIds = new ArrayList<>();
    }

    @Override
    public void assign(Map<String, Object> map) {
        this.activated = (boolean) map.get(ACTIVATED_KEY);
        this.parent = (String) map.get(PARENT_KEY);
        this.children = (ArrayList<String>) map.get(CHILDREN_KEY);
        this.pages = (int) map.get(PAGES_KEY);
        this.hash = (String) map.get(HASH_KEY);
        this.signers = (ArrayList<String>) map.get(SIGNERS_KEY);
        this.sigIds = (ArrayList<String>) map.get(SIGIDS_KEY);
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

            case PAGES_KEY:
                this.pages = Integer.parseInt(value);
                break;

            case SIGIDS_KEY:
                this.sigIds.add(value);
                break;

            default:
                break;
        }
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

            case PAGES_KEY:
                return Integer.toString(this.pages);

            case HASH_KEY:
                return this.hash;

            case SIGNERS_KEY:
                return this.signers.toString();

            case SIGIDS_KEY:
                return this.sigIds.toString();

            default:
                return null;
        }
    }

    private ArrayList<String> toList(String string) {
        return new ArrayList<>(Arrays.asList(string.split(",")));
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
        map.put(PAGES_KEY, this.pages);
        map.put(HASH_KEY, this.hash);
        map.put(SIGNERS_KEY, this.signers);
        map.put(SIGIDS_KEY, this.sigIds);

        return map;
    }

    private void deactivate() {
        this.activated = false;
    }
}
