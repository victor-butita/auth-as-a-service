package com.authservice.core.domain.service

import com.authservice.core.domain.model.User
import java.util.UUID

interface TokenService {
    fun generateAccessToken(user: User, redirectUrl: String? = null): String
    fun generateRefreshToken(user: User): String
    fun validateToken(token: String): Boolean
    fun getUserIdFromToken(token: String): UUID?
}
