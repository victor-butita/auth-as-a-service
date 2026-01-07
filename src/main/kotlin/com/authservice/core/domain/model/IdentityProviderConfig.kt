package com.authservice.core.domain.model

import java.util.UUID

data class IdentityProviderConfig(
    val id: UUID = UUID.randomUUID(),
    val applicationId: UUID,
    val providerName: String, // e.g., "GOOGLE"
    val clientId: String,
    val clientSecret: String,
    val discoveryUrl: String? = null, // for OIDC
    val enabled: Boolean = true
)
