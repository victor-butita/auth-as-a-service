package com.authservice.core.domain.repository

import com.authservice.core.domain.model.Event
import com.authservice.core.domain.model.EventType
import java.util.UUID

interface EventRepository {
    fun save(event: Event): Event
    fun findByApplicationId(applicationId: UUID): List<Event>
    fun countByApplicationIdAndType(applicationId: UUID, type: EventType): Long
    fun countRecentlyActiveUsers(applicationId: UUID, after: java.time.LocalDateTime): Long
}
