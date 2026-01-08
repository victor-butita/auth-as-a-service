package com.authservice.core.application.usecase

import com.authservice.core.domain.repository.ApplicationRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class DeleteApplicationUseCase(
    private val applicationRepository: ApplicationRepository
) {
    fun execute(id: UUID) {
        applicationRepository.deleteById(id)
    }
}
