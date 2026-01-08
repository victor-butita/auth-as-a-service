package com.authservice.core.web.controller

import com.authservice.core.application.dto.UserListResponse
import com.authservice.core.application.usecase.ListUsersUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val listUsersUseCase: ListUsersUseCase
) {
    @GetMapping
    fun listAll(): ResponseEntity<List<UserListResponse>> {
        val response = listUsersUseCase.executeGlobal()
        return ResponseEntity.ok(response)
    }

    @GetMapping("/application/{applicationId}")
    fun listByApplication(@PathVariable applicationId: UUID): ResponseEntity<List<UserListResponse>> {
        val response = listUsersUseCase.execute(applicationId)
        return ResponseEntity.ok(response)
    }
}
