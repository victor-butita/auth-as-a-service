package com.authservice.core.infrastructure.security

import com.authservice.core.domain.service.PasswordHasher
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class BCryptPasswordHasher : PasswordHasher {
    private val passwordEncoder = BCryptPasswordEncoder()

    override fun hash(password: String): String {
        return passwordEncoder.encode(password) ?: throw IllegalStateException("Encoding failed")
    }

    override fun verify(password: String, hashed: String): Boolean {
        return passwordEncoder.matches(password, hashed)
    }
}
