package com.authservice.core.web.controller

import com.authservice.core.application.dto.LoginResponse
import com.authservice.core.application.dto.SsoCallbackRequest
import com.authservice.core.application.usecase.HandleSSOCallbackUseCase
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth/sso")
class SsoController(
    private val handleSSOCallbackUseCase: HandleSSOCallbackUseCase
) {
    @PostMapping("/callback")
    fun callback(
        @RequestBody request: SsoCallbackRequest,
        servletRequest: HttpServletRequest
    ): ResponseEntity<LoginResponse> {
        val ipAddress = servletRequest.remoteAddr
        val userAgent = servletRequest.getHeader("User-Agent")
        val response = handleSSOCallbackUseCase.execute(request, ipAddress, userAgent)
        return ResponseEntity.ok(response)
    }
}
