package com.authservice.core.web.controller

import com.authservice.core.application.dto.CreateTenantRequest
import com.authservice.core.application.dto.TenantResponse
import com.authservice.core.application.usecase.CreateTenantUseCase
import com.authservice.core.application.usecase.GetAllTenantsUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/tenants")
class TenantController(
    private val createTenantUseCase: CreateTenantUseCase,
    private val getAllTenantsUseCase: GetAllTenantsUseCase
) {
    @PostMapping
    fun create(@RequestBody request: CreateTenantRequest): ResponseEntity<TenantResponse> {
        val response = createTenantUseCase.execute(request)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getAll(): ResponseEntity<List<TenantResponse>> {
        return ResponseEntity.ok(getAllTenantsUseCase.execute())
    }
}
