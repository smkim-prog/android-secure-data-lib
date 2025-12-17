package com.ksign.crypto

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * 핵심 암복호화 엔진
 * AES-256-GCM 알고리즘 사용
 * Android Keystore 기반 키 관리
 */
object CryptoEngine {

    private const val KEY_ALIAS = "ksign_crypto_master_key"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val IV_SIZE = 12
    private const val TAG_SIZE = 128

    @Volatile
    private var initialized = false
    private lateinit var secretKey: SecretKey

    /**
     * 초기화 (필요시 자동 호출됨)
     */
    @Synchronized
    internal fun initIfNeeded(context: Context) {
        if (initialized) return

        try {
            secretKey = getOrCreateKey()
            initialized = true
        } catch (e: Exception) {
            throw CryptoException("Failed to initialize CryptoEngine", e)
        }
    }

    /**
     * String 암호화
     * @param plainText 평문
     * @return Base64 인코딩된 암호문 (IV + 암호문 + AuthTag 포함)
     */
    fun encrypt(plainText: String): String {
        checkInitialized()

        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            val iv = cipher.iv
            val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

            // IV + 암호문을 합쳐서 Base64 인코딩
            val combined = iv + encryptedBytes
            return Base64.encodeToString(combined, Base64.DEFAULT)
        } catch (e: Exception) {
            throw CryptoException("Encryption failed", e)
        }
    }

    /**
     * String 복호화
     * @param encryptedText Base64 인코딩된 암호문
     * @return 평문
     */
    fun decrypt(encryptedText: String): String {
        checkInitialized()

        try {
            val combined = Base64.decode(encryptedText, Base64.DEFAULT)

            // IV와 암호문 분리
            val iv = combined.sliceArray(0 until IV_SIZE)
            val encryptedBytes = combined.sliceArray(IV_SIZE until combined.size)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(TAG_SIZE, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

            val decryptedBytes = cipher.doFinal(encryptedBytes)
            return String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            throw CryptoException("Decryption failed", e)
        }
    }

    /**
     * Android Keystore에서 키 가져오거나 생성
     */
    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)

        // 이미 키가 존재하면 반환
        if (keyStore.containsAlias(KEY_ALIAS)) {
            return keyStore.getKey(KEY_ALIAS, null) as SecretKey
        }

        // 키가 없으면 생성
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    /**
     * 초기화 여부 확인
     */
    private fun checkInitialized() {
        if (!initialized) {
            throw IllegalStateException(
                "CryptoEngine is not initialized. " +
                "Please use EncryptedPreferences or call initIfNeeded() first."
            )
        }
    }

    /**
     * 초기화 상태 확인 (테스트용)
     */
    internal fun isInitialized(): Boolean = initialized
}
