package com.gugu.chuman.request_encrpt.security

/**
 * Created by asia on 2020/9/7
 *
 */
object PasswordEncrypt {
    private const val DEFAULT_PUBLIC_KEY =
        """MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCtGIiuszQ8eubb9tWX7N00GD0s
V8930ZMB7aXwxH4vZlomIEjVdz3J5EDkhMvLLkcboFePae3GsIZlD2q4HF1yCrJe
3emoh360/MaZ9/AznP3creL4NCw0v78kRkoeJQ4oifkNy0GhUOA6U23mlgm/h+cq
eC8J5NuuX/somCLMMwIDAQAB"""

    fun encrypt(pwd: String?): String? {
        return RSAUtil.encrypt(pwd, RSAUtil.loadPublicKey(DEFAULT_PUBLIC_KEY))
    }

}