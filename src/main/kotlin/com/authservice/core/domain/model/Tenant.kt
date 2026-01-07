package com.authservice.core.domain.model

import java.util.UUID

data class Tenant(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val contactEmail: String,
    val applications: List<Application> = emptyList()
)
