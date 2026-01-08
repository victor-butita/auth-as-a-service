package com.authservice.core.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class User(
    val id: UUID = UUID.randomUUID(),
    val applicationId: UUID,
    val tenantId: UUID,
    val email: String,
    val passwordHash: String,
    val roles: Set<String> = setOf("USER"),
    val metadata: Map<String, String> = emptyMap(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
