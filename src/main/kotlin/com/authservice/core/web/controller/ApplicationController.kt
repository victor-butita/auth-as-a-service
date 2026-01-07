package com.authservice.core.web.controller

import com.authservice.core.application.dto.ApplicationResponse
import com.authservice.core.application.dto.CreateApplicationRequest
import com.authservice.core.application.usecase.CreateApplicationUseCase
import com.authservice.core.application.usecase.ListApplicationsUseCase
import com.authservice.core.application.usecase.RotateClientSecretUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/applications")
class ApplicationController(
    private val createApplicationUseCase: CreateApplicationUseCase,
    private val listApplicationsUseCase: ListApplicationsUseCase,
    private val rotateClientSecretUseCase: RotateClientSecretUseCase
) {

    @PostMapping
    fun create(@RequestBody request: CreateApplicationRequest): ResponseEntity<ApplicationResponse> {
        val response = createApplicationUseCase.execute(request)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/tenant/{tenantId}")
    fun listByTenant(@PathVariable tenantId: UUID): ResponseEntity<List<ApplicationResponse>> {
        val response = listApplicationsUseCase.execute(tenantId)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{id}/rotate-secret")
    fun rotateSecret(@PathVariable id: UUID): ResponseEntity<ApplicationResponse> {
        val response = rotateClientSecretUseCase.execute(id)
        return ResponseEntity.ok(response)
    }
}
