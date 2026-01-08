package com.authservice.core.application.dto

import java.util.UUID

data class CreateApplicationRequest(
    val tenantId: UUID,
    val name: String,
    val redirectUris: List<String> = emptyList(),
    val roles: List<String> = emptyList(),
    val roleRedirects: Map<String, String> = emptyMap(),
    val identityProviders: List<String> = emptyList(),
    val registrationFields: List<String> = emptyList()
)

data class UpdateApplicationRequest(
    val name: String?,
    val redirectUris: List<String>?,
    val roles: List<String>?,
    val roleRedirects: Map<String, String>?,
    val identityProviders: List<String>?,
    val providerConfigs: List<ProviderUpdateRequest>?,
    val registrationFields: List<String>?
)

data class ProviderUpdateRequest(
    val providerName: String,
    val clientId: String?,
    val clientSecret: String?,
    val discoveryUrl: String?,
    val enabled: Boolean?
)

data class ApplicationResponse(
    val id: UUID,
    val tenantId: UUID,
    val name: String,
    val clientId: String,
    val clientSecret: String,
    val redirectUris: Set<String>,
    val roles: Set<String>,
    val roleRedirects: Map<String, String>,
    val identityProviders: List<String> = emptyList(),
    val registrationFields: List<String> = emptyList(),
    val providerConfigs: List<IdentityProviderConfigResponse> = emptyList()
)

data class IdentityProviderConfigResponse(
    val providerName: String,
    val clientId: String,
    val clientSecret: String,
    val discoveryUrl: String?,
    val enabled: Boolean
)
