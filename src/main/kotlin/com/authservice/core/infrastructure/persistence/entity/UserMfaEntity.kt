package com.authservice.core.infrastructure.persistence.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "user_mfa")
class UserMfaEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "user_id", nullable = false, unique = true)
    val userId: UUID,

    @Column(nullable = false)
    val secret: String,

    @Column(nullable = false)
    val enabled: Boolean = false,

    @ElementCollection
    @CollectionTable(name = "user_mfa_backup_codes", joinColumns = [JoinColumn(name = "mfa_id")])
    @Column(name = "backup_code")
    val backupCodes: List<String> = emptyList()
)
