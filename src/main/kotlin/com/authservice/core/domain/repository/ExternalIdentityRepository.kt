package com.authservice.core.domain.repository

import com.authservice.core.domain.model.ExternalIdentity
import java.util.Optional

interface ExternalIdentityRepository {
    fun findByProviderAndExternalId(provider: String, externalId: String): ExternalIdentity?
    fun save(identity: ExternalIdentity): ExternalIdentity
    fun deleteByUserId(userId: java.util.UUID)
}
