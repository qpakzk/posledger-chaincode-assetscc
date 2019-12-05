package kr.ac.postech.sslab.main;

import kr.ac.postech.sslab.extension.*;
import kr.ac.postech.sslab.nft.NFT;
import org.hyperledger.fabric.shim.ChaincodeStub;
import java.util.List;

public class CustomMain extends Main implements IEERC721, IXNFT {
    private EERC721 eerc721 = new EERC721();
    private DocNFT doc = new DocNFT();
    private SigNFT sig = new SigNFT();

    @Override
    public Response init(ChaincodeStub stub) {
        return super.init(stub);
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        try {
            String func = stub.getFunction();
            List<String> args = stub.getParameters();

            switch (func) {
                case "balanceOf":
                    return this.balanceOf(stub, args);

                case "divide":
                    return this.divide(stub, args);

                case "deactivate":
                    return this.deactivate(stub, args);

                case "query":
                    return this.query(stub, args);

                case "queryHistory":
                    return this.queryHistory(stub, args);

                case "mint":
                    return this.mint(stub, args);

                case "setURI":
                    return this.setURI(stub, args);

                case  "getURI":
                    return this.getURI(stub, args);

                case "setXAttr":
                    return this.setXAttr(stub, args);


                case "getXAttr":
                    return this.getXAttr(stub, args);

                default:
                    return super.invoke(stub);
            }

        } catch (Throwable throwable) {
            return newErrorResponse("FAILURE");
        }
    }

    @Override
    public Response balanceOf(ChaincodeStub stub, List<String> args) {
        return this.eerc721.balanceOf(stub, args);
    }

    @Override
    public Response divide(ChaincodeStub stub, List<String> args) {
        return this.eerc721.divide(stub, args);
    }

    @Override
    public Response deactivate(ChaincodeStub stub, List<String> args) {
        return this.eerc721.deactivate(stub, args);
    }

    @Override
    public Response query(ChaincodeStub stub, List<String> args) {
        return this.eerc721.query(stub, args);
    }

    @Override
    public Response queryHistory(ChaincodeStub stub, List<String> args) {
        return this.eerc721.queryHistory(stub, args);
    }

    @Override
    public Response mint(ChaincodeStub stub, List<String> args) {
        String type = args.get(1).toLowerCase();
        switch (type) {
            case "doc":
                return this.doc.mint(stub, args);

            case "sig":
                return this.sig.mint(stub, args);

            default:
                return super.mint(stub, args);
        }
    }

    @Override
    public Response getURI(ChaincodeStub stub, List<String> args) {
        try {
            String id = args.get(0).toLowerCase();
            NFT nft = NFT.read(stub, id);
            switch (nft.getType()) {
                case "doc":
                    return this.doc.getURI(stub, args);

                case "sig":
                    return this.sig.getURI(stub, args);

                default:
                    throw new Throwable("FAILURE");
            }
        } catch (Throwable throwable) {
            return newErrorResponse("FAILURE");
        }
    }

    @Override
    public Response setURI(ChaincodeStub stub, List<String> args) {
        try {
            String id = args.get(0).toLowerCase();
            NFT nft = NFT.read(stub, id);
            switch (nft.getType()) {
                case "doc":
                    return this.doc.setURI(stub, args);

                case "sig":
                    return this.sig.setURI(stub, args);

                default:
                    throw new Throwable("FAILURE");
            }
        } catch (Throwable throwable) {
            return newErrorResponse("FAILURE");
        }
    }

    @Override
    public Response setXAttr(ChaincodeStub stub, List<String> args) {
        try {
            String id = args.get(0).toLowerCase();
            NFT nft = NFT.read(stub, id);
            switch (nft.getType()) {
                case "doc":
                    return this.doc.setXAttr(stub, args);

                case "sig":
                    return this.sig.setXAttr(stub, args);

                default:
                    throw  new Throwable("FAILURE");
            }
        } catch (Throwable throwable) {
            return newErrorResponse("FAILURE");
        }
    }

    @Override
    public Response getXAttr(ChaincodeStub stub, List<String> args) {
        try {
            String id = args.get(0).toLowerCase();
            NFT nft = NFT.read(stub, id);
            switch (nft.getType()) {
                case "doc":
                    return this.doc.getXAttr(stub, args);

                case "sig":
                    return this.sig.getXAttr(stub, args);

                default:
                    throw  new Throwable("FAILURE");
            }
        } catch (Throwable throwable) {
            return newErrorResponse("FAILURE");
        }
    }

    public static void main(String[] args) {
        new CustomMain().start(args);
    }
}
