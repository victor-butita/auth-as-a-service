package com.authservice.core.web.controller

import com.authservice.core.application.dto.*
import com.authservice.core.application.usecase.ConfirmMfaLoginUseCase
import com.authservice.core.application.usecase.MfaSetupUseCase
import com.authservice.core.application.usecase.MfaVerifyUseCase
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/mfa")
class MfaController(
    private val mfaSetupUseCase: MfaSetupUseCase,
    private val mfaVerifyUseCase: MfaVerifyUseCase,
    private val confirmMfaLoginUseCase: ConfirmMfaLoginUseCase
) {

    @PostMapping("/setup")
    fun setup(@RequestParam email: String): ResponseEntity<MfaSetupResponse> {
        return ResponseEntity.ok(mfaSetupUseCase.execute(email))
    }

    @PostMapping("/activate")
    fun activate(@RequestBody request: MfaVerifyRequest): ResponseEntity<MfaVerifyResponse> {
        return ResponseEntity.ok(mfaVerifyUseCase.execute(request))
    }

    @PostMapping("/verify-login")
    fun verifyLogin(
        @RequestBody request: MfaVerifyRequest,
        servletRequest: HttpServletRequest
    ): ResponseEntity<LoginResponse> {
        val ipAddress = servletRequest.remoteAddr
        val userAgent = servletRequest.getHeader("User-Agent")
        return ResponseEntity.ok(confirmMfaLoginUseCase.execute(request, ipAddress, userAgent))
    }
}
