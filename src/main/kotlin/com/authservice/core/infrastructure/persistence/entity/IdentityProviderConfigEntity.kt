package com.authservice.core.infrastructure.persistence.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "identity_provider_configs")
class IdentityProviderConfigEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "application_id", nullable = false)
    val applicationId: UUID,

    @Column(name = "provider_name", nullable = false)
    val providerName: String,

    @Column(name = "client_id", nullable = false)
    val clientId: String,

    @Column(name = "client_secret", nullable = false)
    val clientSecret: String,

    @Column(name = "discovery_url")
    val discoveryUrl: String? = null,

    @Column(nullable = false)
    val enabled: Boolean = true
)
