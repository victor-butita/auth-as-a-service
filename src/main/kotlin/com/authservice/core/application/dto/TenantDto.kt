package com.authservice.core.application.dto

data class CreateTenantRequest(
    val name: String,
    val contactEmail: String
)

data class TenantResponse(
    val id: java.util.UUID,
    val name: String,
    val contactEmail: String
)
