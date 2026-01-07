package com.authservice.core.domain.repository

import com.authservice.core.domain.model.UserMfa
import java.util.UUID

interface UserMfaRepository {
    fun findByUserId(userId: UUID): UserMfa?
    fun save(userMfa: UserMfa): UserMfa
    fun deleteByUserId(userId: UUID)
}
