# KSign Crypto Library

Android용 간편한 암복호화 라이브러리입니다. SharedPreferences와 Room Database에서 투명한 암호화를 제공합니다.

## 특징

- **AES-256-GCM**: 강력한 암호화 알고리즘
- **Android Keystore**: 하드웨어 기반 안전한 키 관리
- **자동 초기화**: 별도 초기화 코드 불필요
- **투명한 암호화**: 기존 코드 최소 수정
- **간편한 사용**: 직관적인 API

## 설치

### Step 1: JitPack 저장소 추가

`settings.gradle.kts` 파일에 JitPack 저장소를 추가하세요:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### Step 2: 의존성 추가

`build.gradle.kts` (Module: app)에 라이브러리를 추가하세요:

```kotlin
dependencies {
    implementation("com.github.ksign:secure-data:1.0.0")
}
```

## 사용법

### 1. SharedPreferences 암호화

기존 SharedPreferences 대신 `EncryptedPreferences`를 사용하세요.

```kotlin
// 초기화 (자동으로 CryptoEngine 초기화됨)
val encPrefs = EncryptedPreferences(context)

// 데이터 저장 (자동 암호화)
encPrefs.putString("user_token", "secret_token_123")
encPrefs.putInt("user_id", 12345)
encPrefs.putBoolean("is_premium", true)

// 데이터 읽기 (자동 복호화)
val token = encPrefs.getString("user_token")
val userId = encPrefs.getInt("user_id")
val isPremium = encPrefs.getBoolean("is_premium")

// 기타 메서드
encPrefs.remove("user_token")
encPrefs.clear()
encPrefs.contains("user_token")
```

### 2. Room Database 암호화

민감한 필드에 `@Encrypted` 어노테이션을 추가하세요.

#### Entity 정의

```kotlin
@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: Long,
    val email: String,                    // 평문 저장 (검색 가능)
    @Encrypted val password: String,      // 자동 암호화
    @Encrypted val creditCard: String?    // 자동 암호화 (nullable)
)
```

#### Database 설정

```kotlin
@Database(entities = [User::class], version = 1)
@TypeConverters(CryptoTypeConverters::class)  // TypeConverter 추가
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // CryptoEngine 초기화
            CryptoEngine.initIfNeeded(context)

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

#### 사용

```kotlin
val user = User(
    id = 1,
    email = "user@example.com",
    password = "my_password",        // DB 저장 시 자동 암호화
    creditCard = "1234-5678-9012"    // DB 저장 시 자동 암호화
)

// 저장
database.userDao().insert(user)

// 조회 (자동으로 복호화됨)
val savedUser = database.userDao().getById(1)
println(savedUser.password)  // "my_password" (복호화된 상태)
```

### 3. 일반 String 암복호화

어디서든 사용 가능한 범용 암복호화 기능:

```kotlin
// 먼저 초기화 필요
CryptoEngine.initIfNeeded(context)

// 암호화
val encrypted = CryptoEngine.encrypt("민감한 데이터")

// 파일에 저장
File("secret.txt").writeText(encrypted)

// 나중에 읽어서 복호화
val encryptedFromFile = File("secret.txt").readText()
val decrypted = CryptoEngine.decrypt(encryptedFromFile)
```

### 4. 클린 아키텍처에서 사용

```kotlin
// Data Layer - LocalDataSource
class UserLocalDataSource(context: Context) {
    private val encPrefs = EncryptedPreferences(context, "user_data")

    fun saveUserToken(token: String) {
        encPrefs.putString("access_token", token)
    }

    fun getUserToken(): String? {
        return encPrefs.getString("access_token")
    }
}

// DI Module (Hilt/Koin)
@Module
class DataModule {
    @Provides
    @Singleton
    fun provideUserLocalDataSource(
        @ApplicationContext context: Context
    ): UserLocalDataSource = UserLocalDataSource(context)
}
```

## 보안 사양

- **암호화 알고리즘**: AES-256-GCM
- **키 관리**: Android Keystore (하드웨어 보안 모듈)
- **키 길이**: 256bit
- **IV**: 12bytes (GCM 모드)
- **인증 태그**: 128bit

## 주의사항

1. **Application Context 사용**: Activity Context가 아닌 Application Context를 사용하세요.
2. **앱 재설치**: 앱을 재설치하면 Keystore의 키가 삭제되어 기존 암호화된 데이터를 복호화할 수 없습니다.
3. **백업**: 암호화된 데이터는 디바이스 간 이동이 불가능합니다.
4. **성능**: 대용량 데이터 암복호화는 성능에 영향을 줄 수 있으니 백그라운드 스레드에서 처리하세요.

## 라이선스

MIT License

## 기여

이슈 및 PR은 언제나 환영합니다!
