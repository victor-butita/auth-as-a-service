package com.authservice.core.domain.repository

import com.authservice.core.domain.model.IdentityProviderConfig
import java.util.UUID

interface IdentityProviderRepository {
    fun findByApplicationIdAndProviderName(applicationId: UUID, providerName: String): IdentityProviderConfig?
    fun findByApplicationId(applicationId: UUID): List<IdentityProviderConfig>
    fun save(config: IdentityProviderConfig): IdentityProviderConfig
}
