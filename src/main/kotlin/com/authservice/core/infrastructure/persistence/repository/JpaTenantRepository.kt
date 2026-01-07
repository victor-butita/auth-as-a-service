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
        return tenantJpaRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findAll(): List<Tenant> {
        return tenantJpaRepository.findAll().map { it.toDomain() }
    }

    override fun save(tenant: Tenant): Tenant {
        val entity = TenantEntity(
            id = tenant.id,
            name = tenant.name,
            contactEmail = tenant.contactEmail
        )
        return tenantJpaRepository.save(entity).toDomain()
    }

    override fun deleteById(id: UUID) {
        tenantJpaRepository.deleteById(id)
    }

    private fun TenantEntity.toDomain() = Tenant(
        id = this.id,
        name = this.name,
        contactEmail = this.contactEmail
        // Applications mapping can be added if needed, but often we query them separately
    )
}
