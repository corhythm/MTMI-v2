package com.mju.mtmi.util

import android.util.Log
import java.nio.charset.StandardCharsets
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.Key
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Secret {
    const val USER_INFO_PASSWORD_KEY =
        "o9pqYVC9-F8_.PEzEiw!L9F6.AYj9jcfVJ*_i.ifXYnyE68kix@Q2dL6rw*bV-rpdZYwcqZG-jPF-fw3CiJyKsfZ778ks-*jnZn"
}

object AES128 {
    private const val key = Secret.USER_INFO_PASSWORD_KEY
    private val ips: String
    private val keySpec: Key

    init {
        val keyBytes = ByteArray(16)
        val b = key.toByteArray(StandardCharsets.UTF_8)
        System.arraycopy(b, 0, keyBytes, 0, keyBytes.size)
        this.ips = key.substring(0, 16)
        this.keySpec = SecretKeySpec(keyBytes, "AES")
    }

    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class
    )
    fun encrypt(value: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, this.keySpec, IvParameterSpec(this.ips.toByteArray()))
        val encrypted = cipher.doFinal(value.toByteArray(StandardCharsets.UTF_8))
        return String(Base64.getEncoder().encode(encrypted))
    }

    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class
    )
    fun decrypt(value: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, this.keySpec, IvParameterSpec(this.ips.toByteArray(StandardCharsets.UTF_8)))
        val decrypted = Base64.getDecoder().decode(value.toByteArray())
        return String(cipher.doFinal(decrypted), StandardCharsets.UTF_8)
    }
}