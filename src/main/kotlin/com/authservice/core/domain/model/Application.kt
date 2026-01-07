package com.authservice.core.domain.model

import java.util.UUID

data class Application(
    val id: UUID = UUID.randomUUID(),
    val tenantId: UUID,
    val name: String,
    val clientId: String = UUID.randomUUID().toString(),
    val clientSecret: String = UUID.randomUUID().toString(),
    val redirectUris: List<String> = emptyList()
)
