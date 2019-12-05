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
        this.activated = (boolean) map.get("activated");
        this.parent = (String) map.get("parent");
        this.children = (ArrayList<String>) map.get("children");
        this.pages = (int) map.get("pages");
        this.hash = (String) map.get("hash");
        this.signers = (ArrayList<String>) map.get("signers");
        this.sigIds = (ArrayList<String>) map.get("sigIds");
    }

    @Override
    public void setXAttr(String index, String value) {
        switch (index) {
            case "activated":
                this.deactivate();
                break;

            case "parent":
                this.parent = value;
                break;

            case "children":
                this.children = this.toList(value);
                break;

            case "pages":
                this.pages = Integer.parseInt(value);
                break;

            case "sigIds":
                this.sigIds.add(value);
                break;
        }
    }

    @Override
    public String getXAttr(String index) {
        switch (index) {
            case "activated":
                return Boolean.toString(this.activated);

            case "parent":
                return this.parent;

            case "children":
                return this.children.toString();

            case "pages":
                return Integer.toString(this.pages);

            case "hash":
                return this.hash;

            case "signers":
                return this.signers.toString();

            case "sigIds":
                return this.sigIds.toString();
        }

        return null;
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
        map.put("activated", this.activated);
        map.put("parent", this.parent);
        map.put("children", this.children);
        map.put("pages", this.pages);
        map.put("hash", this.hash);
        map.put("signers", this.signers);
        map.put("sigIds", this.sigIds);

        return map;
    }

    private void deactivate() {
        this.activated = false;
    }
}
