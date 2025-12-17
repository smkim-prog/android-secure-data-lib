package com.ksign.crypto

/**
 * Room Database에서 암호화가 필요한 필드에 붙이는 어노테이션
 *
 * 사용 예시:
 * ```
 * @Entity
 * data class User(
 *     @PrimaryKey val id: Long,
 *     val email: String,              // 평문
 *     @Encrypted val password: String // 자동 암호화
 * )
 * ```
 *
 * 주의: @TypeConverters(CryptoTypeConverters::class)를 Database 클래스에 추가해야 합니다.
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Encrypted
