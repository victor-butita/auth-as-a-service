package com.authservice.core.application.dto

import java.util.UUID

data class CreateApplicationRequest(
    val tenantId: UUID,
    val name: String,
    val redirectUris: List<String> = emptyList(),
    val roles: List<String> = emptyList(),
    val roleRedirects: Map<String, String> = emptyMap()
)

data class ApplicationResponse(
    val id: UUID,
    val tenantId: UUID,
    val name: String,
    val clientId: String,
    val clientSecret: String,
    val redirectUris: List<String>,
    val roles: List<String>,
    val roleRedirects: Map<String, String>
)
