package com.authservice.core.infrastructure.persistence.repository

import com.authservice.core.domain.model.Application
import com.authservice.core.domain.repository.ApplicationRepository
import com.authservice.core.infrastructure.persistence.entity.ApplicationEntity
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class JpaApplicationRepository(
    private val applicationJpaRepository: ApplicationJpaRepository,
    private val tenantJpaRepository: TenantJpaRepository
) : ApplicationRepository {

    override fun findById(id: UUID): Application? {
        return applicationJpaRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findByClientId(clientId: String): Application? {
        return applicationJpaRepository.findByClientId(clientId)?.toDomain()
    }

    override fun findByTenantId(tenantId: UUID): List<Application> {
        return applicationJpaRepository.findByTenantId(tenantId).map { it.toDomain() }
    }

    override fun save(application: Application): Application {
        val tenant = tenantJpaRepository.findById(application.tenantId)
            .orElseThrow { IllegalArgumentException("Tenant not found") }

        val entity = applicationJpaRepository.findById(application.id).orElse(
            ApplicationEntity(
                id = application.id,
                tenant = tenant,
                name = application.name,
                clientId = application.clientId,
                clientSecret = application.clientSecret,
                redirectUris = application.redirectUris.toMutableList(),
                roles = application.roles.toMutableList(),
                roleRedirects = application.roleRedirects.toMutableMap()
            )
        ).apply {
            this.name = application.name
            this.redirectUris = application.redirectUris.toMutableList()
            this.roles = application.roles.toMutableList()
            this.roleRedirects = application.roleRedirects.toMutableMap()
        }

        return applicationJpaRepository.save(entity).toDomain()
    }

    override fun deleteById(id: UUID) {
        applicationJpaRepository.deleteById(id)
    }

    private fun ApplicationEntity.toDomain() = Application(
        id = this.id,
        tenantId = this.tenant.id,
        name = this.name,
        clientId = this.clientId,
        clientSecret = this.clientSecret,
        redirectUris = this.redirectUris,
        roles = this.roles,
        roleRedirects = this.roleRedirects
    )
}
