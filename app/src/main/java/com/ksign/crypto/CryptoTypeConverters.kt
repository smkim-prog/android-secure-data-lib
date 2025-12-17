package com.ksign.crypto

import android.content.Context
import androidx.room.TypeConverter

/**
 * Room Database용 TypeConverter
 * @Encrypted 어노테이션이 붙은 필드를 자동으로 암복호화합니다.
 *
 * 사용 방법:
 * ```
 * @Database(entities = [User::class], version = 1)
 * @TypeConverters(CryptoTypeConverters::class)
 * abstract class AppDatabase : RoomDatabase() {
 *     // ...
 *
 *     companion object {
 *         fun getDatabase(context: Context): AppDatabase {
 *             CryptoEngine.initIfNeeded(context) // 초기화
 *             return Room.databaseBuilder(...)
 *                 .build()
 *         }
 *     }
 * }
 * ```
 *
 * 주의사항:
 * - Room의 TypeConverter는 어노테이션 정보를 직접 받을 수 없어서,
 *   @Encrypted 필드는 수동으로 암복호화 메서드를 적용해야 합니다.
 * - 또는 아래 제공하는 확장 함수를 사용하세요.
 */
class CryptoTypeConverters {

    /**
     * 암호화용 TypeConverter
     * Entity의 @Encrypted 필드 타입을 String으로 변경하고
     * get/set에서 이 메서드를 호출하세요.
     */
    @TypeConverter
    fun encryptString(plainText: String?): String? {
        return plainText?.let { CryptoEngine.encrypt(it) }
    }

    /**
     * 복호화용 TypeConverter
     */
    @TypeConverter
    fun decryptString(encryptedText: String?): String? {
        return try {
            encryptedText?.let { CryptoEngine.decrypt(it) }
        } catch (e: Exception) {
            null // 복호화 실패 시 null 반환
        }
    }
}

/**
 * 확장 함수: String을 암호화하여 DB에 저장할 형태로 변환
 */
fun String.encrypt(): String = CryptoEngine.encrypt(this)

/**
 * 확장 함수: DB에서 읽은 암호화된 String을 복호화
 */
fun String.decrypt(): String = CryptoEngine.decrypt(this)

/**
 * 확장 함수: nullable String 암호화
 */
fun String?.encryptOrNull(): String? = this?.let { CryptoEngine.encrypt(it) }

/**
 * 확장 함수: nullable String 복호화
 */
fun String?.decryptOrNull(): String? = try {
    this?.let { CryptoEngine.decrypt(it) }
} catch (e: Exception) {
    null
}
