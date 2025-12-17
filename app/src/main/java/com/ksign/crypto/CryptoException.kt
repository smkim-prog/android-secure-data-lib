package com.ksign.crypto

/**
 * 암복호화 과정에서 발생하는 예외
 */
class CryptoException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
