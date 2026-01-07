package com.authservice.core.infrastructure.persistence.entity

import com.authservice.core.domain.model.EventType
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "events")
class EventEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "application_id", nullable = false)
    val applicationId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: EventType,

    @Column(nullable = false)
    val timestamp: LocalDateTime = LocalDateTime.now(),

    @Column(name = "ip_address")
    val ipAddress: String? = null,

    @Column(name = "user_agent")
    val userAgent: String? = null,

    @ElementCollection
    @CollectionTable(name = "event_metadata", joinColumns = [JoinColumn(name = "event_id")])
    @MapKeyColumn(name = "meta_key")
    @Column(name = "meta_value")
    val metadata: Map<String, String> = emptyMap()
)
