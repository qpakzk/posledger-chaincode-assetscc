package kr.ac.postech.sslab.user;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.bouncycastle.util.io.pem.PemReader;
import org.hyperledger.fabric.protos.msp.Identities.SerializedIdentity;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

public class Address {
	private Address() {
		throw new IllegalStateException("Address class");
	}

    public static String getMyAddress(ChaincodeStub stub) {
        return AddressUtils.getAddressFor(getMyCertificate(stub));
    }

    private static X509Certificate getMyCertificate(ChaincodeStub stub) {
        try {
            SerializedIdentity identity = SerializedIdentity.parseFrom(stub.getCreator());
            StringReader reader = new StringReader(identity.getIdBytes().toStringUtf8());
            PemReader pr = new PemReader(reader);
            byte[] x509Data = pr.readPemObject().getContent();
            CertificateFactory factory = CertificateFactory.getInstance("X509");
            return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(x509Data));
        } catch (IOException | CertificateException e) {
            throw new ChaincodeException("Failed to retrieve certificate of invoking identity", e);
        }
    }
}
