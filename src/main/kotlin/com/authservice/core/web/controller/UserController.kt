package com.authservice.core.web.controller

import com.authservice.core.application.dto.UpdateUserRolesRequest
import com.authservice.core.application.dto.UserResponse
import com.authservice.core.application.usecase.UpdateUserRolesUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val updateUserRolesUseCase: UpdateUserRolesUseCase
) {

    @PutMapping("/roles")
    fun updateRoles(@RequestBody request: UpdateUserRolesRequest): ResponseEntity<UserResponse> {
        val response = updateUserRolesUseCase.execute(request)
        return ResponseEntity.ok(response)
    }
}
