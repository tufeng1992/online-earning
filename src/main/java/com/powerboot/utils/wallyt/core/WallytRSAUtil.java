package com.powerboot.utils.wallyt.core;

import io.netty.util.CharsetUtil;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class WallytRSAUtil {


    /**
     * For creating signature for an request, 用于对发送的请求报文进行签名
     *
     * @param data request data, 请求体, e.g."{\"msgId\":\"999\",\"partnerId\":\"1234567\", ....}"
     * @param priKey the pair of the one (sign pub key) you uploaded, 创建商户上传的签名公钥, 对应私钥
     * @return signature of the request data, you should set it into "signature" field, check our example on document, 返回该请求体的签名
    需要将其设置在向我们发送的报文的 signature 这个属性中, 可以参照文档给出的报文例子
     */
    @SneakyThrows
    public static String signBySHA256WithRSA(String data, String priKey) {
        try {
            Signature signature = Signature.getInstance("SHA256WithRSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(priKey));
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);
            signature.initSign(privateKey);
            signature.update(data.getBytes("UTF-8"));
            return Base64.encodeBase64String(signature.sign());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("RSA signature creating error");
        }
    }

    /**
     * For verifying signature for an request / response, 用于对响应报文, 或通知报文进行验签
     * @param data stuff in "response" or "request" field, 我方向您发送的报文中的"response"或"request"属性下的整个内容, e.g.
    "response":{"code":"200","message":"Success", ...}
     * @param sign stuff in "signature" field, 我方向您发送的报文中的"signature"字段
     * @param pubKey the public signing key which provided form us, 新建商户时向您提供的我方签名公钥
     * @return true if it's valid, 返回"true"表示验签成功
     */
    @SneakyThrows
    public static boolean verifyBySHA256WithRSA(String data, String sign, String pubKey) {
        try {
            Signature signature = Signature.getInstance("SHA256WithRSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(pubKey));
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);
            signature.initVerify(publicKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.decodeBase64(sign.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new Exception("RSA signature verifying error");
        }
    }

    /**
     * Encrypted a field, e.g. "authorizationCode", you should use it's cipher text value for signing a signature, 用于对某个字段进行加密, 例如
     "authorizationCode"这个字段, 需要先对其进行加密获得密文, 然后带上密文去进行签名
     * @param data the plain text for authorizationCode, "authorizationCode"的明文, e.g. AUTH_xxxxxxxx
     * @param pubKey the public encryption key which provided from us, 新建商户时向您提供的我方加密公钥
     * @return cipher text value of the data input, 入参"data"的密文, e.g. QpV1+ihU+UipBqK/1Nbrg0BMyu3Malg==
     */
    @SneakyThrows
    public static String encryptByRSA(String data, String pubKey) {
        try {

            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(pubKey));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            Key publicKey = keyFactory.generatePublic(x509KeySpec);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.encodeBase64String(cipher.doFinal(data.getBytes(CharsetUtil.UTF_8)));
        } catch (Exception e) {
            throw new Exception("RSA encryption error");
        }
    }

    /**
     * Decrypted a field, e.g. "authorizationCode", 用于对字段进行解密, 例如"authorizationCode"这个字段
     *
     * @param cipherText the cipher text of a single field, 某个字段的密文, e.g. QpV1+ihU+UipBqK/1Nbrg0BMyu3Malg==
     * @param priKey the pair of the one (encrypt pub key) you uploaded to us, 创建商户时上传的加密公钥的, 对应私钥
     * @return plain text of the cipher text input, 入参"cipherText"的明文, e.g. AUTH_xxxxxxxx
     */
    @SneakyThrows
    public static String decryptByRSA(String cipherText, String priKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.decodeBase64(priKey)));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(Base64.decodeBase64(cipherText)));
        } catch (Exception e) {
            throw new Exception("RSA decryption error");
        }
    }
}
