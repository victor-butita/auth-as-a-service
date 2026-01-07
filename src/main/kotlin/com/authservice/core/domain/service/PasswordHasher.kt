package com.authservice.core.domain.service

interface PasswordHasher {
    fun hash(password: String): String
    fun verify(password: String, hashed: String): Boolean
}
