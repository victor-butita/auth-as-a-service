package com.authservice.core.application.dto

import java.util.UUID

data class CreateApplicationRequest(
    val tenantId: UUID,
    val name: String,
    val redirectUris: List<String> = emptyList()
)

data class ApplicationResponse(
    val id: UUID,
    val tenantId: UUID,
    val name: String,
    val clientId: String,
    val clientSecret: String,
    val redirectUris: List<String>
)
