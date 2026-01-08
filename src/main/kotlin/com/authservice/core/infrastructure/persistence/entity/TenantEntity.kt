package com.authservice.core.infrastructure.persistence.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "tenants")
class TenantEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, unique = true)
    var contactEmail: String,

    @OneToMany(mappedBy = "tenant", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var applications: MutableList<ApplicationEntity> = mutableListOf(),

    @Column(nullable = false)
    var deleted: Boolean = false
)
