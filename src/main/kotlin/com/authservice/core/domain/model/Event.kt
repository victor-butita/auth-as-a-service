package com.authservice.core.domain.model

import java.time.LocalDateTime
import java.util.UUID

enum class EventType {
    USER_REGISTERED,
    USER_LOGGED_IN,
    TOKEN_REFRESHED,
    CLIENT_SECRET_ROTATED
}

data class Event(
    val id: UUID = UUID.randomUUID(),
    val applicationId: UUID,
    val type: EventType,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val ipAddress: String? = null,
    val userAgent: String? = null,
    val metadata: Map<String, String> = emptyMap()
)
