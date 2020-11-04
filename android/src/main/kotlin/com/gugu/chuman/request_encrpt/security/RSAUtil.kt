package com.gugu.chuman.request_encrpt.security

import android.util.Base64
import android.util.Log
import java.io.InputStream
import java.security.*
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

/**
 * Created by asia on 2020/9/7
 * RSA加密工具
 * 使用public key加密，使用private key 解密
 */
object RSAUtil {
    private const val RSA = "RSA"

    /**
     * 用[publicKey]加载数据[data]
     * 返回加密成功后的数据，或加密失败返回null
     */
    @JvmStatic
    fun encrypt(data: ByteArray?, publicKey: PublicKey?): ByteArray? {
        if (data == null || publicKey == null) return null
        return try {
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            cipher.doFinal(data)
        } catch (e: Exception) {
            Log.e("RSAUtil", "encrypt", e)
            null
        }
    }

    /**
     * 用[publicKey]加密[data]后，返回base64编码后的字符串
     */
    @JvmStatic
    fun encrypt(data: String?, publicKey: PublicKey?): String? {
        val result = encrypt(data?.toByteArray(), publicKey) ?: return null
        return Base64.encodeToString(result, Base64.URL_SAFE or Base64.NO_WRAP)
    }

    /**
     * 用[privateKey]解密加密过的数据[data]
     */
    @JvmStatic
    fun decrypt(data: ByteArray?, privateKey: PrivateKey?): ByteArray? {
        return try {
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            cipher.doFinal(data)
        } catch (e: java.lang.Exception) {
            null
        }
    }

    /**
     * 对[data]base64解码后，再使用[privateKey]解密码，返回解密后的字符串
     */
    fun decrypt(data: String?, privateKey: PrivateKey?): String? {
        val base64 = Base64.decode(data, Base64.URL_SAFE or Base64.NO_WRAP)
        val result = decrypt(base64, privateKey) ?: return null
        return String(result)
    }

    @JvmStatic
    fun loadPublicKey(stream: InputStream): PublicKey {
        return loadPublicKey(readKey(stream))
    }

    @Throws(RuntimeException::class)
    @JvmStatic
    fun loadPublicKey(publicKeyStr: String?): PublicKey {
        return try {
            val buffer: ByteArray = Base64.decode(publicKeyStr, Base64.DEFAULT)
            val keyFactory: KeyFactory = KeyFactory.getInstance(RSA)
            val keySpec = X509EncodedKeySpec(buffer)
            keyFactory.generatePublic(keySpec) as RSAPublicKey
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("无此算法", e)
        } catch (e: InvalidKeySpecException) {
            throw RuntimeException("公钥非法", e)
        } catch (e: NullPointerException) {
            throw RuntimeException("公钥数据为空", e)
        }
    }

    /**
     * 从字符串中加载私钥<br></br>
     * 加载时使用的是PKCS8EncodedKeySpec（PKCS#8编码的Key指令）。
     *
     * @param privateKeyStr
     * @return
     * @throws Exception
     */
    @Throws(java.lang.Exception::class)
    fun loadPrivateKey(privateKeyStr: String?): PrivateKey? {
        return try {
            val buffer: ByteArray = Base64.decode(privateKeyStr, Base64.DEFAULT)
            val keySpec = PKCS8EncodedKeySpec(buffer)
            val keyFactory = KeyFactory.getInstance(RSA)
            keyFactory.generatePrivate(keySpec) as RSAPrivateKey
        } catch (e: NoSuchAlgorithmException) {
            throw java.lang.Exception("无此算法")
        } catch (e: InvalidKeySpecException) {
            throw java.lang.Exception("私钥非法")
        } catch (e: java.lang.NullPointerException) {
            throw java.lang.Exception("私钥数据为空")
        }
    }

    private fun readKey(stream: InputStream): String? {
        val reader = stream.bufferedReader()
        return reader.lineSequence().filter { !it.startsWith("--") }.joinToString("\r")
    }
}