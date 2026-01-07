package com.authservice.core.application.usecase

import com.authservice.core.application.dto.MfaVerifyRequest
import com.authservice.core.application.dto.MfaVerifyResponse
import com.authservice.core.domain.repository.UserMfaRepository
import com.authservice.core.domain.repository.UserRepository
import com.authservice.core.domain.service.MfaService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MfaVerifyUseCase(
    private val userRepository: UserRepository,
    private val userMfaRepository: UserMfaRepository,
    private val mfaService: MfaService
) {
    @Transactional
    fun execute(request: MfaVerifyRequest): MfaVerifyResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("User not found")

        val mfa = userMfaRepository.findByUserId(user.id)
            ?: throw IllegalArgumentException("MFA not set up for this user")

        val isValid = mfaService.verifyCode(mfa.secret, request.code)
        
        if (isValid) {
            userMfaRepository.save(mfa.copy(enabled = true))
            return MfaVerifyResponse(true, "MFA successfully enabled")
        } else {
            return MfaVerifyResponse(false, "Invalid MFA code")
        }
    }
}
