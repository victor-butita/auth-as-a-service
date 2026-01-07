package com.authservice.core.application.usecase

import com.authservice.core.application.dto.MfaSetupResponse
import com.authservice.core.domain.model.UserMfa
import com.authservice.core.domain.repository.UserMfaRepository
import com.authservice.core.domain.repository.UserRepository
import com.authservice.core.domain.service.MfaService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class MfaSetupUseCase(
    private val userRepository: UserRepository,
    private val userMfaRepository: UserMfaRepository,
    private val mfaService: MfaService
) {
    @Transactional
    fun execute(email: String): MfaSetupResponse {
        val user = userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("User not found")

        val secret = mfaService.generateSecret()
        val qrCodeUrl = mfaService.getQrCodeUrl(secret, user.email, "AuthService")

        // Store or update MFA secret (not enabled yet)
        val existingMfa = userMfaRepository.findByUserId(user.id)
        if (existingMfa != null) {
            userMfaRepository.save(existingMfa.copy(secret = secret, enabled = false))
        } else {
            userMfaRepository.save(UserMfa(userId = user.id, secret = secret, enabled = false))
        }

        return MfaSetupResponse(secret = secret, qrCodeUrl = qrCodeUrl)
    }
}
