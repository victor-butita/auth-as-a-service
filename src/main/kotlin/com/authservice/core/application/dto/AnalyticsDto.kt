package com.authservice.core.application.dto

import java.time.LocalDateTime
import java.util.UUID

data class DashboardAnalyticsResponse(
    val applicationId: UUID,
    val totalUsers: Long,
    val totalRegistrations: Long,
    val totalLogins: Long,
    val activeSessions: Long,
    val systemHealth: Map<String, String>,
    val recentEvents: List<EventDto>
)

data class EventDto(
    val id: UUID,
    val type: String,
    val timestamp: LocalDateTime,
    val ipAddress: String? = null,
    val userAgent: String? = null,
    val metadata: Map<String, String>
)
