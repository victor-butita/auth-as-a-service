package com.authservice.core.infrastructure.persistence.repository

import com.authservice.core.domain.model.ExternalIdentity
import com.authservice.core.domain.repository.ExternalIdentityRepository
import com.authservice.core.infrastructure.persistence.entity.ExternalIdentityEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

interface ExternalIdentityJpaRepository : JpaRepository<ExternalIdentityEntity, UUID> {
    fun findByProviderAndExternalId(provider: String, externalId: String): Optional<ExternalIdentityEntity>
    fun deleteByUserId(userId: UUID)
}

@Repository
class JpaExternalIdentityRepository(
    private val jpaRepository: ExternalIdentityJpaRepository
) : ExternalIdentityRepository {

    override fun findByProviderAndExternalId(provider: String, externalId: String): ExternalIdentity? {
        return jpaRepository.findByProviderAndExternalId(provider, externalId).map { it.toDomain() }.orElse(null)
    }

    override fun save(identity: ExternalIdentity): ExternalIdentity {
        val entity = ExternalIdentityEntity(
            id = identity.id,
            userId = identity.userId,
            provider = identity.provider,
            externalId = identity.externalId,
            externalEmail = identity.externalEmail
        )
        return jpaRepository.save(entity).toDomain()
    }

    override fun deleteByUserId(userId: UUID) {
        jpaRepository.deleteByUserId(userId)
    }

    private fun ExternalIdentityEntity.toDomain() = ExternalIdentity(
        id = this.id,
        userId = this.userId,
        provider = this.provider,
        externalId = this.externalId,
        externalEmail = this.externalEmail
    )
}
