package com.authservice.core.domain.repository

import com.authservice.core.domain.model.Tenant
import java.util.UUID

interface TenantRepository {
    fun findById(id: UUID): Tenant?
    fun findAll(): List<Tenant>
    fun save(tenant: Tenant): Tenant
    fun deleteById(id: UUID)
}
