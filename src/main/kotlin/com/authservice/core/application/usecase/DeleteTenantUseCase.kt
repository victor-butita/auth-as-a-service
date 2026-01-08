package com.authservice.core.application.usecase

import com.authservice.core.domain.repository.TenantRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class DeleteTenantUseCase(
    private val tenantRepository: TenantRepository
) {
    fun execute(id: UUID) {
        tenantRepository.deleteById(id)
    }
}
