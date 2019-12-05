package kr.ac.postech.sslab.nft;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.postech.sslab.adapter.XAttr;
import kr.ac.postech.sslab.type.URI;
import org.hyperledger.fabric.shim.ChaincodeStub;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class NFT {
    private String id;
    private String type;
    private String owner;
    private String approvee;
    private XAttr xattr;
    private URI uri;

    public NFT() {}

    private NFT(String id, String type, String owner, String approvee, XAttr xattr, URI uri) {
        this.id = id;
        this.type = type;
        this.owner = owner;
        this.approvee = approvee;
        this.xattr = xattr;
        this.uri = uri;
    }

    public void mint(ChaincodeStub stub, String id, String type, String owner, XAttr xattr, URI uri) throws JsonProcessingException {
        this.id = id;
        this.type = type;
        this.owner = owner;
        this.approvee = "";
        this.xattr = xattr;
        this.uri = uri;

        stub.putStringState(this.id, this.toJSONString());
    }

    public static NFT read(ChaincodeStub stub, String id) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = stub.getStringState(id);

        Map<String, Object> map =
                mapper.readValue(json, new TypeReference<HashMap<String, Object>>(){});

        String type = (String) map.get("type");
        String owner = (String) map.get("owner");
        String approvee = (String) map.get("approvee");

        XAttr xattr = null;
        if (map.containsKey("xattr")) {
            Map<String, Object> xattrMap = (HashMap<String, Object>) map.get("xattr");
            xattr = new XAttr();
            xattr.assign(type, xattrMap);
        }

        URI uri = null;
        if (map.containsKey("uri")) {
            Map<String, String> uriMap = (HashMap<String, String>) map.get("uri");
            uri = new URI();
            uri.assign(uriMap);
        }

        return new NFT(id, type, owner, approvee, xattr, uri);
    }

    public void burn(ChaincodeStub stub, String id) {
        stub.delState(id);
    }

    public String getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

    public void setOwner(ChaincodeStub stub, String owner) throws JsonProcessingException {
        this.owner = owner;
        stub.putStringState(this.id, this.toJSONString());
    }

    public String getOwner() {
        return this.owner;
    }

    public void setApprovee(ChaincodeStub stub, String approvee) throws JsonProcessingException {
        this.approvee = approvee;
        stub.putStringState(this.id, this.toJSONString());
    }

    public String getApprovee() {
        return this.approvee;
    }

    public void setXAttr(ChaincodeStub stub, String index, String value) throws JsonProcessingException {
        this.xattr.setXAttr(index, value);
        stub.putStringState(this.id, this.toJSONString());
    }

    public void setXAttr(ChaincodeStub stub, XAttr xattr) throws JsonProcessingException {
        this.xattr = xattr;
        stub.putStringState(this.id, this.toJSONString());
    }

    public String getXAttr(String index) {
        return this.xattr.getXAttr(index);
    }

    public XAttr getXAttr() {
        return this.xattr;
    }

    public void setURI(ChaincodeStub stub, String index, String value) throws JsonProcessingException {
        switch (index) {
            case "path":
                this.uri.setPath(value);
                break;

            case "hash":
                this.uri.setHash(value);
                break;
        }

        stub.putStringState(this.id, this.toJSONString());
    }

    public void setURI(ChaincodeStub stub, URI uri) throws JsonProcessingException {
        this.uri = uri;
        stub.putStringState(this.id, this.toJSONString());
    }

    public String getURI(String index) {
        switch (index) {
            case "path":
                return this.uri.getPath();

            case "hash":
                return uri.getHash();

            default:
                return null;
        }
    }

    public URI getURI() {
        return this.uri;
    }

    public String toJSONString() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this.toMap());
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.id);
        map.put("type", this.type);
        map.put("owner", this.owner);
        map.put("approvee", this.approvee);

        if (this.xattr != null) {
            map.put("xattr", this.xattr.toMap());
        }

        if (this.uri != null) {
            map.put("uri", this.uri.toMap());
        }

        return map;
    }
}