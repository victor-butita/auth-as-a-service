package com.authservice.core.application.usecase

import com.authservice.core.application.dto.TenantResponse
import com.authservice.core.domain.repository.TenantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetAllTenantsUseCase(
    private val tenantRepository: TenantRepository
) {
    @Transactional(readOnly = true)
    fun execute(): List<TenantResponse> {
        return tenantRepository.findAll().map {
            TenantResponse(
                id = it.id,
                name = it.name,
                contactEmail = it.contactEmail
            )
        }
    }
}
