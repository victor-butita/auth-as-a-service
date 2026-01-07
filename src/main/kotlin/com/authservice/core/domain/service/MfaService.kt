package com.authservice.core.domain.service

interface MfaService {
    fun generateSecret(): String
    fun getQrCodeUrl(secret: String, email: String, issuer: String): String
    fun verifyCode(secret: String, code: String): Boolean
}
