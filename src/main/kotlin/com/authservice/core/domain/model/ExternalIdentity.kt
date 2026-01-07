package com.authservice.core.domain.model

import java.util.UUID

data class ExternalIdentity(
    val id: UUID = UUID.randomUUID(),
    val userId: UUID,
    val provider: String, // e.g., "GOOGLE", "AZURE_AD"
    val externalId: String, // The unique ID from the provider (e.g., 'sub' in OIDC)
    val externalEmail: String? = null
)
