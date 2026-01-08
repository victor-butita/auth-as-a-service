package com.authservice.core.infrastructure.persistence.repository

import com.authservice.core.domain.model.IdentityProviderConfig
import com.authservice.core.domain.repository.IdentityProviderRepository
import com.authservice.core.infrastructure.persistence.entity.IdentityProviderConfigEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

interface IdentityProviderJpaRepository : JpaRepository<IdentityProviderConfigEntity, UUID> {
    fun findByApplicationIdAndProviderName(applicationId: UUID, providerName: String): Optional<IdentityProviderConfigEntity>
    fun findByApplicationId(applicationId: UUID): List<IdentityProviderConfigEntity>
}

@Repository
class JpaIdentityProviderRepository(
    private val jpaRepository: IdentityProviderJpaRepository
) : IdentityProviderRepository {

    override fun findByApplicationIdAndProviderName(applicationId: UUID, providerName: String): IdentityProviderConfig? {
        return jpaRepository.findByApplicationIdAndProviderName(applicationId, providerName).map { it.toDomain() }.orElse(null)
    }

    override fun findByApplicationId(applicationId: UUID): List<IdentityProviderConfig> {
        return jpaRepository.findByApplicationId(applicationId).map { it.toDomain() }
    }

    override fun save(config: IdentityProviderConfig): IdentityProviderConfig {
        val entity = IdentityProviderConfigEntity(
            id = config.id,
            applicationId = config.applicationId,
            providerName = config.providerName,
            clientId = config.clientId,
            clientSecret = config.clientSecret,
            discoveryUrl = config.discoveryUrl,
            enabled = config.enabled
        )
        return jpaRepository.save(entity).toDomain()
    }

    private fun IdentityProviderConfigEntity.toDomain() = IdentityProviderConfig(
        id = this.id,
        applicationId = this.applicationId,
        providerName = this.providerName,
        clientId = this.clientId,
        clientSecret = this.clientSecret,
        discoveryUrl = this.discoveryUrl,
        enabled = this.enabled
    )
}
