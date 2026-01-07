package com.authservice.core.domain.model

import java.util.UUID

data class UserMfa(
    val id: UUID = UUID.randomUUID(),
    val userId: UUID,
    val secret: String, // Base32 encoded secret
    val enabled: Boolean = false,
    val backupCodes: List<String> = emptyList()
)
