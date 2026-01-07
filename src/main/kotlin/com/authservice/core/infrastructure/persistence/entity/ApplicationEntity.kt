package com.authservice.core.infrastructure.persistence.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "applications")
class ApplicationEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    var tenant: TenantEntity,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, unique = true)
    var clientId: String = UUID.randomUUID().toString(),

    @Column(nullable = false)
    var clientSecret: String = UUID.randomUUID().toString(),

    @ElementCollection
    @CollectionTable(name = "application_redirect_uris", joinColumns = [JoinColumn(name = "application_id")])
    @Column(name = "redirect_uri")
    var redirectUris: MutableList<String> = mutableListOf(),

    @ElementCollection
    @CollectionTable(name = "application_roles", joinColumns = [JoinColumn(name = "application_id")])
    @Column(name = "role")
    var roles: MutableList<String> = mutableListOf(),

    @ElementCollection
    @CollectionTable(name = "application_role_redirects", joinColumns = [JoinColumn(name = "application_id")])
    @MapKeyColumn(name = "role_name")
    @Column(name = "redirect_uri")
    var roleRedirects: MutableMap<String, String> = mutableMapOf()
)
