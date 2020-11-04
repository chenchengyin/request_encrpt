package com.gugu.chuman.request_encrpt.security;


import android.util.Base64;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 编解码相关工具类
 * Created by imkarl on 2017/09/7
 */
public class EncodeUtils {

    private EncodeUtils() {
    }

    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final Charset UTF_16BE = Charset.forName("UTF-16BE");
    public static final Charset UTF_16LE = Charset.forName("UTF-16LE");
    public static final Charset UTF_16 = Charset.forName("UTF-16");

    private static final Pattern PATTERN_UNICODE = Pattern.compile("\\\\u([0-9A-Fa-f]{4})");


    /**
     * Base64编码
     *
     * @param data 待编码的字符
     * @return 编码结果
     */
    
    public static String encodeBase64( byte[] data) {
        try {
            return Base64.encodeToString(data, Base64.NO_WRAP);
        } catch (Exception e) {
            Log.e("ENCRPT","error",e);
            return null;
        }
    }

    /**
     * Base64解码
     *
     * @param data 待解码的字符
     * @return 解码结果
     */
    
    public static byte[] decodeBase64( String data) {
        try {
            return Base64.decode(data, Base64.NO_WRAP);
        } catch (Exception e) {
            Log.e("ENCRPT","error",e);
            return null;
        }
    }

    /**
     * URL编码【UTF-8】
     *
     * @param data 待编码的字符
     * @return 编码结果
     */
    public static String encodeUrl(final String data) {
        return encodeUrl(data, UTF_8);
    }

    /**
     * URL编码
     *
     * @param data    待编码的字符
     * @param charset 字符集
     * @return 编码结果，若编码失败则直接将data原样返回
     */
    public static String encodeUrl(final String data, final Charset charset) {
        try {
            return URLEncoder.encode(data, charset.name());
        } catch (UnsupportedEncodingException e) {
            return data;
        }
    }

    /**
     * URL解码【UTF-8】
     *
     * @param data 待解码的字符
     * @return 解码结果
     */
    public static String decodeUrl(final String data) {
        return decodeUrl(data, UTF_8);
    }

    /**
     * URL解码
     *
     * @param data    待解码的字符
     * @param charset 字符集
     * @return 解码结果，若解码失败则直接将data原样返回
     */
    public static String decodeUrl(final String data, final Charset charset) {
        try {
            return URLDecoder.decode(data, charset.name());
        } catch (UnsupportedEncodingException e) {
            return data;
        }
    }

    /**
     * Unicode编码
     *
     * @param data 待编码的字符
     * @return 编码结果，若编码失败则直接将data原样返回
     */
    public static String encodeUnicode(String data) {
        StringBuilder unicodeBytes = new StringBuilder();
        for (char ch : data.toCharArray()) {
            if (ch < 10) {
                unicodeBytes.append("\\u000").append(Integer.toHexString(ch));
                continue;
            }

            final Character.UnicodeBlock ub = Character.UnicodeBlock.of(ch);
            if (ub == Character.UnicodeBlock.BASIC_LATIN) {
                // 英文及数字等
                unicodeBytes.append(ch);
            } else {
                // to Unicode
                String hex = Integer.toHexString(ch);
                if (hex.length() == 1) {
                    unicodeBytes.append("\\u000").append(hex);
                } else if (hex.length() == 2) {
                    unicodeBytes.append("\\u00").append(hex);
                } else if (hex.length() == 3) {
                    unicodeBytes.append("\\u0").append(hex);
                } else if (hex.length() == 4) {
                    unicodeBytes.append("\\u").append(hex);
                }
            }
        }
        return unicodeBytes.toString();
    }

    /**
     * Unicode解码
     *
     * @param data 待解码的字符
     * @return 解码结果，若解码失败则直接将data原样返回
     */
    public static String decodeUnicode(String data) {
        if (!data.contains("\\u")) {
            return data;
        }

        StringBuffer buf = new StringBuffer();
        Matcher matcher = PATTERN_UNICODE.matcher(data);
        while (matcher.find()) {
            try {
                int cp = Integer.parseInt(matcher.group(1), 16);
                matcher.appendReplacement(buf, "");
                buf.appendCodePoint(cp);
            } catch (NumberFormatException ignored) {
            }
        }
        matcher.appendTail(buf);
        return buf.toString();
    }

    /**
     * 字节数组转16进制字符串
     *
     * @param data 待转换的字节数组
     * @return 16进制字符串
     */
    
    public static String bytesToHexString( byte[] data) {
        if (data == null || data.length <= 0) {
            return null;
        }

        StringBuilder hexBuilder = new StringBuilder();
        for (byte b : data) {
            String hv = Integer.toHexString(b & 0xFF);
            if (hv.length() < 2) {
                hexBuilder.append(0);
            }
            hexBuilder.append(hv);
        }
        return hexBuilder.toString();
    }

    /**
     * 十六进制字符串转bytes
     *
     * @param src 16进制字符串
     * @return 字节数组
     */
    public static byte[] hexStringToBytes(String src) {
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            ret[i] = Integer.valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return ret;
    }
}
