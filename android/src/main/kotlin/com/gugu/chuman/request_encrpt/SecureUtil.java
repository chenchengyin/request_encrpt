package com.gugu.chuman.request_encrpt;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import com.gugu.chuman.request_encrpt.security.EncodeUtils;
import com.gugu.chuman.request_encrpt.security.EncryptUtils;

import java.nio.charset.Charset;

/**
 * 加密工具包
 */
public class SecureUtil {
    public static final String USER_ACCESS_KEY = "0b379ed3f4f361a68859dece9ee632fe";
    public static final String USER_SECRET_KEY = "d2a88d717683c58b66965c8187adb4f5";
    private static final Charset DEFAULT_CHARSET = EncodeUtils.ISO_8859_1;

    /**
     * Creates Signature From Query and Post Data
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static String getSignature(String message) {
        String str = USER_ACCESS_KEY + EncodeUtils.encodeUnicode(message);
        Log.e("auth", "str: " +str);
        //LogUtils.i("getSignature() original " + str);
        str = encrypt(str, USER_SECRET_KEY);
        Log.e("auth", "encrypt str: " +str);
        try {
            byte[] signature = EncryptUtils.hmacSHA1(USER_SECRET_KEY.getBytes(DEFAULT_CHARSET), str.getBytes(DEFAULT_CHARSET));
            str = EncodeUtils.encodeBase64(signature);
            //LogUtils.e("str="+str);
            Log.e("auth", "encodeBase64 str: " +str);
            return str;
        } catch (Exception e) {
            Log.e("ENCRPT","error",e);
        }
        return null;
    }

    /**
     * Encrypt
     */
    private static String encrypt(String data, String key) {
        key = EncryptUtils.md5((key.getBytes(DEFAULT_CHARSET)));
        // Correct Formatting of the String encoding is correct
        data = EncodeUtils.encodeBase64(("0000000000" + data).getBytes(DEFAULT_CHARSET));

        StringBuilder str = new StringBuilder();
        final int dataLen = data.length();
        final int keyLen = key.length();
        for (int i = 0; i < dataLen; i++) {
            char ch = key.charAt(i % keyLen);

            int char1 = data.charAt(i);
            int char2 = ch % 256;
            str.append((char) (char1 + char2));
        }
        return EncodeUtils.encodeBase64((str.toString().getBytes(DEFAULT_CHARSET)));
    }

}

