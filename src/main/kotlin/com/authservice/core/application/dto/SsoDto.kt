package com.authservice.core.application.dto

import java.util.UUID

data class SsoCallbackRequest(
    val applicationId: UUID,
    val provider: String,
    val externalId: String? = null,
    val email: String? = null,
    val fullName: String? = null,
    val idToken: String? = null,
    val redirectUri: String? = null
)
