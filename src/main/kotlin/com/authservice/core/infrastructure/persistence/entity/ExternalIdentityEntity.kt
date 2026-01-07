package com.authservice.core.infrastructure.persistence.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "external_identities", uniqueConstraints = [
    UniqueConstraint(columnNames = ["provider", "external_id"])
])
class ExternalIdentityEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(nullable = false)
    val provider: String,

    @Column(name = "external_id", nullable = false)
    val externalId: String,

    @Column(name = "external_email")
    val externalEmail: String? = null
)
