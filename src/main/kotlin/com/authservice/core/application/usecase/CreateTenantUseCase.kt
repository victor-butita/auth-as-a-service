package com.authservice.core.application.usecase

import com.authservice.core.application.dto.CreateTenantRequest
import com.authservice.core.application.dto.TenantResponse
import com.authservice.core.domain.model.Tenant
import com.authservice.core.domain.repository.TenantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateTenantUseCase(
    private val tenantRepository: TenantRepository
) {
    @Transactional
    fun execute(request: CreateTenantRequest): TenantResponse {
        val tenant = Tenant(
            name = request.name,
            contactEmail = request.contactEmail
        )
        val savedTenant = tenantRepository.save(tenant)
        return TenantResponse(
            id = savedTenant.id,
            name = savedTenant.name,
            contactEmail = savedTenant.contactEmail
        )
    }
}
