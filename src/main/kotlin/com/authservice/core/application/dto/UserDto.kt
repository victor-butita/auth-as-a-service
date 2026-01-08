package com.authservice.core.application.dto

import java.util.UUID

data class RegisterUserRequest(
    val applicationId: UUID,
    val email: String,
    val password: String,
    val roles: Set<String>? = null,
    val metadata: Map<String, String>? = null
)

data class UserResponse(
    val id: UUID,
    val email: String,
    val roles: Set<String>,
    val metadata: Map<String, String>? = null
)

data class LoginRequest(
    val email: String,
    val password: String,
    val applicationId: UUID? = null
)

data class IdentifyUserRequest(
    val email: String
)

data class LoginResponse(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val mfaRequired: Boolean = false,
    val mfaToken: String? = null, // Temporary token to complete MFA
    val redirectUrl: String? = null,
    val user: UserResponse? = null
)

data class UserListResponse(
    val id: UUID,
    val email: String,
    val roles: Set<String>,
    val providers: List<String>,
    val createdAt: String,
    val applicationId: UUID,
    val applicationName: String
)
