package com.authservice.core.application.dto

import java.util.UUID

data class SsoCallbackRequest(
    val applicationId: UUID,
    val provider: String,
    val externalId: String,
    val email: String,
    val fullName: String? = null
)
