package com.authservice.core.application.usecase

import com.authservice.core.application.dto.DashboardAnalyticsResponse
import com.authservice.core.application.dto.EventDto
import com.authservice.core.domain.model.EventType
import com.authservice.core.domain.repository.EventRepository
import com.authservice.core.domain.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class GetAnalyticsUseCase(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository,
    private val applicationRepository: com.authservice.core.domain.repository.ApplicationRepository,
    private val tenantRepository: com.authservice.core.domain.repository.TenantRepository
) {
    fun execute(applicationId: UUID): DashboardAnalyticsResponse {
        val totalUsers = userRepository.countByApplicationId(applicationId)
        val totalRegistrations = eventRepository.countByApplicationIdAndType(applicationId, EventType.USER_REGISTERED)
        val totalLogins = eventRepository.countByApplicationIdAndType(applicationId, EventType.USER_LOGGED_IN)
        
        println("Analytics DEBUG: AppId=$applicationId, Users=$totalUsers, Regs=$totalRegistrations, Logins=$totalLogins")

        val thirtyMinutesAgo = java.time.LocalDateTime.now().minusMinutes(30)
        val activeSessions = eventRepository.countRecentlyActiveUsers(applicationId, thirtyMinutesAgo)

        val systemHealth = mapOf(
            "Auth Service" to "Online",
            "Database Cluster" to "Online",
            "Redis Cache" to "Online",
            "MFA Provider" to "Online"
        )

        val recentEventsRaw = eventRepository.findByApplicationId(applicationId)
        println("Activities DEBUG: AppId=$applicationId, RawEventsFound=${recentEventsRaw.size}")

        val recentEvents = recentEventsRaw.take(10).map {
            EventDto(
                id = it.id,
                type = it.type.name,
                timestamp = it.timestamp,
                ipAddress = it.ipAddress,
                userAgent = it.userAgent,
                metadata = it.metadata
            )
        }

        return DashboardAnalyticsResponse(
            applicationId = applicationId,
            totalUsers = totalUsers,
            totalRegistrations = totalRegistrations,
            totalLogins = totalLogins,
            activeSessions = activeSessions,
            systemHealth = systemHealth,
            recentEvents = recentEvents
        )
    }

    fun getRawDebugInfo(): Map<String, Any> {
        val apps = applicationRepository.findByTenantId(tenantRepository.findAll().firstOrNull()?.id ?: UUID.randomUUID())
        val debugStats = apps.map { app ->
            mapOf(
                "appId" to app.id,
                "name" to app.name,
                "users" to userRepository.countByApplicationId(app.id),
                "registrations" to eventRepository.countByApplicationIdAndType(app.id, EventType.USER_REGISTERED)
            )
        }
        return mapOf(
            "timestamp" to java.time.LocalDateTime.now(),
            "apps" to debugStats,
            "note" to "Total data across the system."
        )
    }
}
