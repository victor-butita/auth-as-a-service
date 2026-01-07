package com.authservice.core.application.dto

data class MfaSetupResponse(
    val secret: String,
    val qrCodeUrl: String
)

data class MfaVerifyRequest(
    val email: String,
    val code: String
)

data class MfaVerifyResponse(
    val success: Boolean,
    val message: String
)
