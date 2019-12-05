package kr.ac.postech.sslab.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.ac.postech.sslab.type.IType;
import kr.ac.postech.sslab.type.Document;
import kr.ac.postech.sslab.type.Signature;
import java.util.List;
import java.util.Map;

public class XAttrAdapter implements IXAttr {
    private IType xattr;

    XAttrAdapter(String type) {
        switch (type) {
            case "doc":
                this.xattr = new Document();
                break;

            case "sig":
                this.xattr = new Signature();
                break;

			default:
				break;
        }
    }

    public void assign(Map<String, Object> map) {
        this.xattr.assign(map);
    }

    public void assign(List<String> args) {
        this.xattr.assign(args);
    }

    @Override
    public void setXAttr(String index, String value) {
        this.xattr.setXAttr(index, value);
    }

    @Override
    public String getXAttr(String index) {
        return this.xattr.getXAttr(index);
    }

    @Override
    public String toJSONString() throws JsonProcessingException {
        return this.xattr.toJSONString();
    }

    @Override
    public Map<String, Object> toMap() {
        return this.xattr.toMap();
    }
 }
