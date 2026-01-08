package com.authservice.core.infrastructure.persistence.repository

import com.authservice.core.domain.model.Tenant
import com.authservice.core.domain.repository.TenantRepository
import com.authservice.core.infrastructure.persistence.entity.TenantEntity
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class JpaTenantRepository(
    private val tenantJpaRepository: TenantJpaRepository
) : TenantRepository {

    override fun findById(id: UUID): Tenant? {
        return tenantJpaRepository.findById(id).filter { !it.deleted }.orElse(null)?.toDomain()
    }

    override fun findAll(): List<Tenant> {
        return tenantJpaRepository.findAll().filter { !it.deleted }.map { it.toDomain() }
    }

    override fun save(tenant: Tenant): Tenant {
        val entity = tenantJpaRepository.findById(tenant.id).orElse(
            TenantEntity(
                id = tenant.id,
                name = tenant.name,
                contactEmail = tenant.contactEmail,
                deleted = tenant.deleted
            )
        ).apply {
            this.name = tenant.name
            this.contactEmail = tenant.contactEmail
            this.deleted = tenant.deleted
        }
        return tenantJpaRepository.save(entity).toDomain()
    }

    override fun deleteById(id: UUID) {
        tenantJpaRepository.findById(id).ifPresent {
            it.deleted = true
            tenantJpaRepository.save(it)
        }
    }

    private fun TenantEntity.toDomain() = Tenant(
        id = this.id,
        name = this.name,
        contactEmail = this.contactEmail,
        deleted = this.deleted
    )
}
