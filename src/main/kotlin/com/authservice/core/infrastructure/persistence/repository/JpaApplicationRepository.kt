package com.authservice.core.infrastructure.persistence.repository

import com.authservice.core.domain.model.Application
import com.authservice.core.domain.repository.ApplicationRepository
import com.authservice.core.infrastructure.persistence.entity.ApplicationEntity
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
@Transactional
class JpaApplicationRepository(
    private val applicationJpaRepository: ApplicationJpaRepository,
    private val tenantJpaRepository: TenantJpaRepository
) : ApplicationRepository {

    override fun findById(id: UUID): Application? {
        return applicationJpaRepository.findById(id).filter { !it.deleted }.orElse(null)?.toDomain()
    }

    override fun findByClientId(clientId: String): Application? {
        return applicationJpaRepository.findByClientId(clientId)?.takeIf { !it.deleted }?.toDomain()
    }

    override fun findByTenantId(tenantId: UUID): List<Application> {
        return applicationJpaRepository.findByTenantId(tenantId).filter { !it.deleted }.map { it.toDomain() }
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
                roleRedirects = application.roleRedirects.toMutableMap(),
                registrationFields = application.registrationFields.toMutableList(),
                deleted = application.deleted
            )
        ).apply {
            this.name = application.name
            
            this.redirectUris.clear()
            this.redirectUris.addAll(application.redirectUris)
            
            this.roles.clear()
            this.roles.addAll(application.roles)
            
            this.roleRedirects.clear()
            this.roleRedirects.putAll(application.roleRedirects)
            
            this.registrationFields.clear()
            this.registrationFields.addAll(application.registrationFields)
            
            this.deleted = application.deleted
        }

        return applicationJpaRepository.save(entity).toDomain()
    }

    override fun deleteById(id: UUID) {
        applicationJpaRepository.findById(id).ifPresent {
            it.deleted = true
            applicationJpaRepository.save(it)
        }
    }

    private fun ApplicationEntity.toDomain() = Application(
        id = this.id,
        tenantId = this.tenant.id,
        name = this.name,
        clientId = this.clientId,
        clientSecret = this.clientSecret,
        redirectUris = this.redirectUris,
        roles = this.roles,
        roleRedirects = this.roleRedirects,
        registrationFields = this.registrationFields,
        deleted = this.deleted
    )
}
