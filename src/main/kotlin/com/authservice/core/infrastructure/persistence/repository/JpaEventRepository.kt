package com.authservice.core.infrastructure.persistence.repository

import com.authservice.core.domain.model.Event
import com.authservice.core.domain.model.EventType
import com.authservice.core.domain.repository.EventRepository
import com.authservice.core.infrastructure.persistence.entity.EventEntity
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class JpaEventRepository(
    private val eventJpaRepository: EventJpaRepository
) : EventRepository {

    override fun save(event: Event): Event {
        val entity = EventEntity(
            id = event.id,
            applicationId = event.applicationId,
            type = event.type,
            timestamp = event.timestamp,
            ipAddress = event.ipAddress,
            userAgent = event.userAgent,
            metadata = event.metadata
        )
        return eventJpaRepository.save(entity).toDomain()
    }

    override fun findByApplicationId(applicationId: UUID): List<Event> {
        return eventJpaRepository.findByApplicationIdOrderByTimestampDesc(applicationId).map { it.toDomain() }
    }

    override fun findByUserId(userId: UUID): List<Event> {
        return eventJpaRepository.findByUserIdMetadata(userId.toString()).map { it.toDomain() }
    }

    override fun countByApplicationIdAndType(applicationId: UUID, type: EventType): Long {
        return eventJpaRepository.countByApplicationIdAndType(applicationId, type)
    }

    override fun countRecentlyActiveUsers(applicationId: UUID, after: java.time.LocalDateTime): Long {
        return eventJpaRepository.countByApplicationIdAndTypeAndTimestampAfter(
            applicationId, 
            EventType.USER_LOGGED_IN, 
            after
        )
    }

    private fun EventEntity.toDomain() = Event(
        id = this.id,
        applicationId = this.applicationId,
        type = this.type,
        timestamp = this.timestamp,
        ipAddress = this.ipAddress,
        userAgent = this.userAgent,
        metadata = this.metadata
    )
}
