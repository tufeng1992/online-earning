package com.powerboot.utils;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * @create 2021-03-09 17:38
 */
public class SecurityUtil {

    public static void main(String[] args) {
        try {
            System.out.println(encryptInitiatorPassword("Jab2c0217!"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String encryptInitiatorPassword(String password) {
        String encryptedPassword = null;
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            byte[] input = password.getBytes();

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
            FileInputStream fin = new FileInputStream(new File("src/main/resources/cert/cert.cer"));
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) cf.generateCertificate(fin);
            PublicKey pk = certificate.getPublicKey();
            cipher.init(Cipher.ENCRYPT_MODE, pk);

            byte[] cipherText = cipher.doFinal(input);

            // Convert the resulting encrypted byte array into a string using base64 encoding
            encryptedPassword = Base64.encode(cipherText);

        } catch (Exception ex) {
        }

        return encryptedPassword;
    }
}
