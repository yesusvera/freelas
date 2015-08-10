// $Id: X509Util.java 13992 2011-10-07 17:29:58Z mike $

package org.faceless.pdf2.viewer3.util;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;

import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateIssuerName;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateSubjectName;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

/**
 * This class contains one method used by the KeyStoreManager class to create new
 * X.509 Certificates. It accesses the sun.security.x509 package, which isn't
 * allowed in an untrusted environment, so it's in a separate class so we can
 * fail gracefully.
 */
public final class X509Util {

    /**
     * Create an X.509 Certificate - calls into the sun.security.x509 package
     */
    public X509Certificate generateCertificate(String dn, KeyPair pair, int days, String algorithm) throws GeneralSecurityException {
        if (dn == null || dn.length() == 0) {
            throw new GeneralSecurityException("Empty distinguished name!");
        }
        try {
            PrivateKey privkey = pair.getPrivate();
            X509CertInfo info = new X509CertInfo();
            Date fromdate = new Date();
            Date todate = new Date(fromdate.getTime() + days * 86400000l);
            CertificateValidity interval = new CertificateValidity(fromdate, todate);
            CertificateSerialNumber serial = new CertificateSerialNumber(new BigInteger(64, new Random()));
            X500Name owner = new X500Name(dn);
            info.set(X509CertInfo.VALIDITY, interval);
            info.set(X509CertInfo.SERIAL_NUMBER, serial);
            info.set(X509CertInfo.SUBJECT, new CertificateSubjectName(owner));
            info.set(X509CertInfo.ISSUER, new CertificateIssuerName(owner));
            info.set(X509CertInfo.KEY, new CertificateX509Key(pair.getPublic()));
            info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
            AlgorithmId algo = new AlgorithmId(AlgorithmId.md5WithRSAEncryption_oid);
            info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algo));

            X509CertImpl cert = new X509CertImpl(info);     // Test sign to get algorithm used
            cert.sign(privkey, algorithm);

            algo = (AlgorithmId)cert.get(X509CertImpl.SIG_ALG);
            info.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, algo);
            cert = new X509CertImpl(info);
            cert.sign(privkey, algorithm);
            return cert;
        } catch (IOException e) {
            GeneralSecurityException e2 = new GeneralSecurityException("Failed generating certificate for DN="+dn);
            e2.initCause(e);
            throw e2;
        }
    }

}
