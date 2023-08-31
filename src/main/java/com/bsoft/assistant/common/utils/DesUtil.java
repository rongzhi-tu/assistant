package com.bsoft.assistant.common.utils;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
 
public class DesUtil {

    // 对称加密，这里不用在意为什么写了两行，都是一样的,
    private static String encryptKey = "6c12b11d";
    private static String decryptKey = "6c12b11d";

    // 向量/偏移量
    private static String ivStr = "166290ac";
    private static byte[] iv = ivStr.getBytes();


    //加密
    public static String encryptDES(String encryptString) throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
        byte[] encryptedData = cipher.doFinal(encryptString.getBytes());

        return Base64.encode(encryptedData);
    }


    //解密
    public static String decryptDES(String decryptString) throws Exception {
        byte[] byteMi = new Base64().decode(decryptString);
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
        byte decryptedData[] = cipher.doFinal(byteMi);

        return new String(decryptedData);
    }

    public static void main(String[] args) throws Exception {
        String text = "sa";
        String result1 = DesUtil.encryptDES(text);
        String result2 = DesUtil.decryptDES(result1);
        System.out.println(result1);
        System.out.println(result2);

    }
    
}