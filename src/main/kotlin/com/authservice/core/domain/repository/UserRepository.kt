package com.authservice.core.domain.repository

import com.authservice.core.domain.model.User
import java.util.UUID

interface UserRepository {
    fun findById(id: UUID): User?
    fun findByEmail(email: String): User?
    fun findByEmailAndTenantId(email: String, tenantId: UUID): User?
    fun findByApplicationId(applicationId: UUID): List<User>
    fun findAll(): List<User>
    fun countByApplicationId(applicationId: UUID): Long
    fun save(user: User): User
    fun deleteById(id: UUID)
}
