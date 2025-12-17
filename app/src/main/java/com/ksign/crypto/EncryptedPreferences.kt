package com.ksign.crypto

import android.content.Context
import android.content.SharedPreferences

/**
 * 자동 암복호화를 지원하는 SharedPreferences 래퍼
 * 모든 데이터는 저장 시 자동으로 암호화되고, 읽을 때 자동으로 복호화됩니다.
 *
 * @param context Application Context
 * @param name SharedPreferences 파일명 (기본값: "encrypted_prefs")
 */
class EncryptedPreferences(
    private val context: Context,
    private val name: String = "encrypted_prefs"
) {

    private val prefs: SharedPreferences

    init {
        // CryptoEngine 자동 초기화
        CryptoEngine.initIfNeeded(context.applicationContext)
        prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    /**
     * String 값 저장 (자동 암호화)
     */
    fun putString(key: String, value: String?) {
        if (value == null) {
            prefs.edit().remove(key).apply()
            return
        }

        try {
            val encrypted = CryptoEngine.encrypt(value)
            prefs.edit().putString(key, encrypted).apply()
        } catch (e: Exception) {
            throw CryptoException("Failed to encrypt and save string for key: $key", e)
        }
    }

    /**
     * String 값 읽기 (자동 복호화)
     */
    fun getString(key: String, defaultValue: String? = null): String? {
        val encrypted = prefs.getString(key, null) ?: return defaultValue

        return try {
            CryptoEngine.decrypt(encrypted)
        } catch (e: Exception) {
            // 복호화 실패 시 기본값 반환 (키가 변경되었거나 데이터 손상)
            defaultValue
        }
    }

    /**
     * Int 값 저장 (String으로 변환 후 암호화)
     */
    fun putInt(key: String, value: Int) {
        putString(key, value.toString())
    }

    /**
     * Int 값 읽기 (복호화 후 Int로 변환)
     */
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return getString(key)?.toIntOrNull() ?: defaultValue
    }

    /**
     * Long 값 저장 (String으로 변환 후 암호화)
     */
    fun putLong(key: String, value: Long) {
        putString(key, value.toString())
    }

    /**
     * Long 값 읽기 (복호화 후 Long으로 변환)
     */
    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return getString(key)?.toLongOrNull() ?: defaultValue
    }

    /**
     * Float 값 저장 (String으로 변환 후 암호화)
     */
    fun putFloat(key: String, value: Float) {
        putString(key, value.toString())
    }

    /**
     * Float 값 읽기 (복호화 후 Float로 변환)
     */
    fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return getString(key)?.toFloatOrNull() ?: defaultValue
    }

    /**
     * Boolean 값 저장 (String으로 변환 후 암호화)
     */
    fun putBoolean(key: String, value: Boolean) {
        putString(key, value.toString())
    }

    /**
     * Boolean 값 읽기 (복호화 후 Boolean으로 변환)
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return getString(key)?.toBoolean() ?: defaultValue
    }

    /**
     * 특정 키 존재 여부 확인
     */
    fun contains(key: String): Boolean {
        return prefs.contains(key)
    }

    /**
     * 특정 키 삭제
     */
    fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    /**
     * 모든 데이터 삭제
     */
    fun clear() {
        prefs.edit().clear().apply()
    }

    /**
     * 모든 키 목록 반환 (디버깅용)
     */
    fun getAllKeys(): Set<String> {
        return prefs.all.keys
    }
}
