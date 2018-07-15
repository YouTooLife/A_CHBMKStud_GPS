package net.youtoolife.chbmkstud;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

/**
 * Created by youtoolife on 5/12/18.
 */

public class RSAIsa {

    private static String keyNormalize(String key) {
        String result = "";
        String[] arr = key.split(":");
        for (String a:arr)
            result += a;
        return result;
    }

    public static byte[] decrypt(String key) throws Exception {

        BigInteger modulus = new BigInteger(
                keyNormalize(XA.b(XA.MA)),
                16);
        BigInteger exp = new BigInteger(
                keyNormalize(XA.b(XA.EA)),
                16);

        RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(modulus, exp);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privKey = kf.generatePrivate(keySpec);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privKey);

        byte[] decodedStr = Base64.decode(key, Base64.DEFAULT);
        byte[] plainText = cipher.doFinal(decodedStr);

        return plainText;
    }

    public static String rsaEncrypt(String text) {
        String result = "";
        try {
            byte[] b2 = encrypt(text.getBytes("UTF-8"));
            String s2 = Base64.encodeToString(b2, Base64.CRLF);
            result = s2;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  result;
    }

    public static String rsaDecrypt(String text) {
        String result = "";
        try {
            byte[] b3 = decrypt(text);
            String s3 = new String(b3, "UTF-8");
            result = s3;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  result;
    }

    private static byte[] encrypt(byte[] b1) throws Exception {
        BigInteger modulus = new BigInteger( keyNormalize(XA.b(XA.MA)) ,
                16);
        BigInteger pubExp = new BigInteger("010001", 16);

        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, pubExp);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey publicKey = kf.generatePublic(keySpec);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] plainText = cipher.doFinal(b1);

        return plainText;
    }
}
