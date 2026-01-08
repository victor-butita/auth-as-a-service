package com.authservice.core.web.controller

import com.authservice.core.application.dto.LoginRequest
import com.authservice.core.application.dto.LoginResponse
import com.authservice.core.application.dto.RegisterUserRequest
import com.authservice.core.application.dto.UserResponse
import com.authservice.core.application.usecase.LoginUseCase
import com.authservice.core.application.usecase.RegisterUserUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val registerUserUseCase: RegisterUserUseCase,
    private val loginUseCase: LoginUseCase,
    private val identifyUserUseCase: com.authservice.core.application.usecase.IdentifyUserUseCase
) {

    @PostMapping("/register")
    fun register(
        @RequestBody request: RegisterUserRequest,
        servletRequest: jakarta.servlet.http.HttpServletRequest
    ): ResponseEntity<UserResponse> {
        val ipAddress = servletRequest.remoteAddr
        val userAgent = servletRequest.getHeader("User-Agent")
        val response = registerUserUseCase.execute(request, ipAddress, userAgent)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequest,
        servletRequest: jakarta.servlet.http.HttpServletRequest
    ): ResponseEntity<LoginResponse> {
        val ipAddress = servletRequest.remoteAddr
        val userAgent = servletRequest.getHeader("User-Agent")
        val response = loginUseCase.execute(request, ipAddress, userAgent)
        return ResponseEntity.ok(response)
    }
    @PostMapping("/identify")
    fun identify(
        @RequestBody request: com.authservice.core.application.dto.IdentifyUserRequest
    ): ResponseEntity<UserResponse> {
        val response = identifyUserUseCase.execute(request.email)
        return ResponseEntity.ok(response)
    }
}
