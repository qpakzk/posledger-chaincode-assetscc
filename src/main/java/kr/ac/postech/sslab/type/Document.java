package kr.ac.postech.sslab.type;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Document extends EType implements IType {
    private int pages;

    private String hash;
    private ArrayList<String> signers;
    private ArrayList<String> sigIds;

	private static final String PAGES_KEY = "pages";
	private static final String HASH_KEY = "hash";
	private static final String SIGNERS_KEY = "signers";
	private static final String SIG_IDS_KEY = "sigIds";

    @Override
    public void assign(List<String> args) {
        super.assign(args);
        this.pages = Integer.parseInt(args.get(0));
        this.hash = args.get(1);
        this.signers = this.toList(args.get(2));
        this.sigIds = new ArrayList<>();
    }

    @Override
    public void assign(Map<String, Object> map) {
        super.assign(map);
        this.pages = (int) map.get(PAGES_KEY);
        this.hash = (String) map.get(HASH_KEY);
        this.signers = (ArrayList<String>) map.get(SIGNERS_KEY);
        this.sigIds = (ArrayList<String>) map.get(SIG_IDS_KEY);
    }

    @Override
    public void setXAttr(String index, String value) {
        switch (index) {
            case PAGES_KEY:
                this.pages = Integer.parseInt(value);
                break;

            case SIG_IDS_KEY:
                this.sigIds.add(value);
                break;

            default:
                super.setXAttr(index, value);
                break;
        }
    }

    @Override
    public String getXAttr(String index) {
        switch (index) {
            case PAGES_KEY:
                return Integer.toString(this.pages);

            case HASH_KEY:
                return this.hash;

            case SIGNERS_KEY:
                return this.signers.toString();

            case SIG_IDS_KEY:
                return this.sigIds.toString();

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
        map.put(PAGES_KEY, this.pages);
        map.put(HASH_KEY, this.hash);
        map.put(SIGNERS_KEY, this.signers);
        map.put(SIG_IDS_KEY, this.sigIds);

        return map;
    }
}
