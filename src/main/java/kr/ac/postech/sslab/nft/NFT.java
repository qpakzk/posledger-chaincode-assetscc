package kr.ac.postech.sslab.nft;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;


public class NFT {
    private static ObjectMapper mapper = new ObjectMapper();

    private BigInteger tokenId;
    private String type;
    private String owner;
    private String approvee;
    private Map<String, Object> xattr;
    private Map<String, String> uri;

    public NFT() {}

    private NFT(BigInteger tokenId, String type, String owner, String approvee, Map<String, Object> xattr, Map<String, String> uri) {
        this.tokenId = tokenId;
        this.type = type;
        this.owner = owner;
        this.approvee = approvee;
        this.xattr = xattr;
        this.uri = uri;
    }

    public boolean mint(ChaincodeStub stub, BigInteger tokenId, String type, String owner, Map<String, Object> xattr, Map<String, String> uri) throws JsonProcessingException {
        this.tokenId = tokenId;
        this.type = type;
        this.owner = owner;
        this.approvee = "";
        this.xattr = xattr;
        this.uri = uri;

        stub.putStringState(this.tokenId.toString(), this.toJSONString());
        return true;
    }

    public static NFT read(ChaincodeStub stub, BigInteger tokenId) throws IOException {
        String json = stub.getStringState(tokenId.toString());

        Map<String, Object> map =
                mapper.readValue(json, new TypeReference<HashMap<String, Object>>(){});

        String type = (String) map.get("type");
        String owner = (String) map.get("owner");
        String approvee = (String) map.get("approvee");

        Map<String, Object> xattr
                = map.containsKey("xattr") ? (HashMap<String, Object>) map.get("xattr") : null;

        Map<String, String> uri
                = map.containsKey("uri") ? (HashMap<String, String>) map.get("uri") : null;

        return new NFT(tokenId, type, owner, approvee, xattr, uri);
    }

    public boolean burn(ChaincodeStub stub, BigInteger tokenId) {
        stub.delState(tokenId.toString());
        return true;
    }

    public BigInteger getId() {
        return this.tokenId;
    }

    public String getType() {
        return this.type;
    }

    public boolean setOwner(ChaincodeStub stub, String owner) throws JsonProcessingException {
        this.owner = owner;
        stub.putStringState(this.tokenId.toString(), this.toJSONString());
        return true;
    }

    public String getOwner() {
        return this.owner;
    }

    public boolean setApprovee(ChaincodeStub stub, String approvee) throws JsonProcessingException {
        this.approvee = approvee;
        stub.putStringState(this.tokenId.toString(), this.toJSONString());
        return true;
    }

    public String getApprovee() {
        return this.approvee;
    }

    public boolean setXAttr(ChaincodeStub stub, String index, Object value) throws JsonProcessingException {
        xattr.put(index, value);
        stub.putStringState(this.tokenId.toString(), this.toJSONString());
        return true;
    }

    public Object getXAttr(String index) {
        return xattr.get(index);
    }

    public Map<String, Object> getXAttr() {
        return xattr;
    }

    public boolean setURI(ChaincodeStub stub, String index, String value) throws JsonProcessingException {
        uri.put(index, value);
        stub.putStringState(this.tokenId.toString(), this.toJSONString());
        return true;
    }

    public String getURI(String index) {
        return uri.get(index);
    }

    public Map<String, String> getURI() {
        return uri;
    }

    public String toJSONString() throws JsonProcessingException {
        return mapper.writeValueAsString(this.toMap());
    }

    public Map<String, Object> toMap() throws JsonProcessingException {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.tokenId);
        map.put("type", this.type);
        map.put("owner", this.owner);
        map.put("approvee", this.approvee);

        if (this.xattr != null) {
            map.put("xattr", mapper.writeValueAsString(xattr));
        }

        if (this.uri != null) {
            map.put("uri", mapper.writeValueAsString(uri));
        }

        return map;
    }
}
