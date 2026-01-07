package com.authservice.core.domain.repository

import com.authservice.core.domain.model.Application
import java.util.UUID

interface ApplicationRepository {
    fun findById(id: UUID): Application?
    fun findByClientId(clientId: String): Application?
    fun findByTenantId(tenantId: UUID): List<Application>
    fun save(application: Application): Application
    fun deleteById(id: UUID)
}
