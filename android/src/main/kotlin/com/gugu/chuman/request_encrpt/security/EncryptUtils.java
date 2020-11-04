package com.gugu.chuman.request_encrpt.security;

/**
 * Date: 2020/11/4 6:08 PM
 * Author: chenchengyin
 */


import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.DigestInputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加解密相关工具类
 * Created by imkarl on 2017/09/7.
 */
public class EncryptUtils {

    private EncryptUtils() {
    }

    /**
     * MD5加密
     *
     * @param data 待加密的字符串
     * @return 32位MD5校验码
     */
    
    public static String md5( String data) {
        return md5(data.getBytes());
    }

    /**
     * MD5加密
     *
     * @param data 待加密的字节数组
     * @return 32位MD5校验码
     */
    
    public static String md5( byte[] data) {
        return EncodeUtils.bytesToHexString(hashTemplate("MD5", data));
    }

    /**
     * MD5加密
     *
     * @param file 待加密的文件
     * @return 32位MD5校验码
     */
    
    public static String md5( File file) {
        FileInputStream fis = null;
        DigestInputStream digestInputStream;
        try {
            fis = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("MD5");
            digestInputStream = new DigestInputStream(fis, md);
            byte[] buffer = new byte[256 * 1024];
            while (true) {
                if (!(digestInputStream.read(buffer) > 0)) {
                    break;
                }
            }
            md = digestInputStream.getMessageDigest();
            return EncodeUtils.bytesToHexString(md.digest());
        } catch (Exception e) {
            Log.e("EncryptUtils","error",e);
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * SHA1加密
     *
     * @param data 明文
     * @return 密文
     */
    
    public static String sha1( String data) {
        return sha1(data.getBytes());
    }

    /**
     * SHA1加密
     *
     * @param data 明文
     * @return 密文
     */
    
    public static String sha1( byte[] data) {
        return EncodeUtils.bytesToHexString(hashTemplate("SHA1", data));
    }

    /**
     * SHA256加密
     *
     * @param data 明文
     * @return 密文
     */
    
    public static String sha256( String data) {
        return sha256(data.getBytes());
    }

    /**
     * SHA256加密
     *
     * @param data 明文
     * @return 密文
     */
    
    public static String sha256( byte[] data) {
        return EncodeUtils.bytesToHexString(hashTemplate("SHA-256", data));
    }

    /**
     * HmacMD5加密
     *
     * @param key  秘钥
     * @param data 明文
     * @return 密文
     */
    
    public static String hmacMD5ToString( String key,  String data) {
        return hmacMD5ToString(key.getBytes(), data.getBytes());
    }

    /**
     * HmacMD5加密
     *
     * @param key  秘钥
     * @param data 明文
     * @return 密文
     */
    
    public static String hmacMD5ToString( byte[] key,  byte[] data) {
        return EncodeUtils.bytesToHexString(hmacMD5(key, data));
    }

    /**
     * HmacMD5加密
     *
     * @param key  秘钥
     * @param data 明文
     * @return 密文
     */
    
    public static byte[] hmacMD5( String key,  String data) {
        return hmacMD5(key.getBytes(), data.getBytes());
    }

    /**
     * HmacMD5加密
     *
     * @param key  秘钥
     * @param data 明文
     * @return 密文
     */
    
    public static byte[] hmacMD5( byte[] key,  byte[] data) {
        return hmacTemplate("HmacMD5", key, data);
    }

    /**
     * HmacSHA1加密
     *
     * @param key  秘钥
     * @param data 明文
     * @return 密文
     */
    
    public static String hmacSHA1ToString( String key,  String data) {
        return hmacSHA1ToString(key.getBytes(), data.getBytes());
    }

    /**
     * HmacSHA1加密
     *
     * @param key  秘钥
     * @param data 明文
     * @return 密文
     */
    
    public static String hmacSHA1ToString( byte[] key,  byte[] data) {
        return EncodeUtils.bytesToHexString(hmacSHA1(key, data));
    }

    /**
     * HmacSHA1加密
     *
     * @param key  秘钥
     * @param data 明文
     * @return 密文
     */
    
    public static byte[] hmacSHA1( String key,  String data) {
        return hmacSHA1(key.getBytes(), data.getBytes());
    }

    /**
     * HmacSHA1加密
     *
     * @param key  秘钥
     * @param data 明文
     * @return 密文
     */
    
    public static byte[] hmacSHA1( byte[] key,  byte[] data) {
        return hmacTemplate("HmacSHA1", key, data);
    }

    /**
     * HmacSHA256加密
     *
     * @param key  秘钥
     * @param data 明文
     * @return 密文
     */
    public static String hmacSHA256ToString( String key,  String data) {
        return EncodeUtils.bytesToHexString(hmacSHA256(key, data));
    }

    /**
     * HmacSHA256加密
     *
     * @param key  秘钥
     * @param data 明文
     * @return 密文
     */
    public static byte[] hmacSHA256( String key, String data) {
        return hmacSHA256(key.getBytes(), data.getBytes());
    }

    /**
     * HmacSHA256加密
     *
     * @param key  秘钥
     * @param data 明文
     * @return 密文
     */
    public static byte[] hmacSHA256( byte[] key,  byte[] data) {
        return hmacTemplate("HmacSHA256", key, data);
    }

    /**
     * hash加密模板
     *
     * @param algorithm 加密算法
     * @param data      待加密的数据
     * @return 密文字节数组
     */
    
    private static byte[] hashTemplate( String algorithm,  byte[] data) {
        try {
            if (data.length > 0) {
                MessageDigest md = MessageDigest.getInstance(algorithm);
                md.update(data);
                return md.digest();
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("EncryptUtils","error",e);
        }
        return null;
    }

    /**
     * Hmac加密模板
     *
     * @param algorithm 加密算法
     * @param key       秘钥
     * @param data      待加密的数据
     * @return 密文字节数组
     */
    
    private static byte[] hmacTemplate( String algorithm,  byte[] key,  byte[] data) {
        try {
            if (key.length > 0 && data.length > 0) {
                Mac mac = Mac.getInstance(algorithm);
                SecretKeySpec secretKey = new SecretKeySpec(key, algorithm);
                mac.init(secretKey);
                return mac.doFinal(data);
            }
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            Log.e("EncryptUtils","error",e);
        }
        return null;
    }

    /**
     * AES加密字符串
     *
     * @param pwd     密码
     * @param content 待加密内容
     * @return 加密结果
     */
    public static String encryptAES(String pwd, String content) {
        try {
            byte[] rawKey = deriveKeyInsecurely(pwd, 32).getEncoded();
            SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
            Cipher cipher = null;
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
            byte[] encrypted = cipher.doFinal(content.getBytes());
            return EncodeUtils.bytesToHexString(encrypted);
        } catch (Exception e) {
            Log.e("EncryptUtils","error",e);
        }
        return null;
    }

    /**
     * AES解密字符串
     *
     * @param pwd  密码
     * @param data 加密字符串
     * @return 解密字符串
     */
    public static String decryptAES(String pwd, String data) {
        try {
            byte[] rawKey = deriveKeyInsecurely(pwd, 32).getEncoded();
            SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
            Cipher cipher = null;
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
            byte[] decrypted = cipher.doFinal(EncodeUtils.hexStringToBytes(data));
            return new String(decrypted);
        } catch (Exception e) {
            Log.e("EncryptUtils","error",e);
        }
        return null;

    }

    private static SecretKey deriveKeyInsecurely(String password, int keySizeInBytes) {
        byte[] passwordBytes = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            passwordBytes = password.getBytes(Charset.forName("US-ASCII"));
        }
        return new SecretKeySpec(
                InsecureSHA1PRNGKeyDerivator.deriveInsecureKey(passwordBytes, keySizeInBytes),
                "AES");
    }
}
