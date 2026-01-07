package com.authservice.core.infrastructure.security

import com.authservice.core.domain.model.User
import com.authservice.core.domain.service.TokenService
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtTokenService(
    @Value("\${auth.jwt.secret}") private val secret: String,
    @Value("\${auth.jwt.access-token-expiration-ms}") private val accessTokenExpiration: Long,
    @Value("\${auth.jwt.refresh-token-expiration-ms}") private val refreshTokenExpiration: Long
) : TokenService {

    private val signingKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray())
    }

    override fun generateAccessToken(user: User, redirectUrl: String?): String {
        return generateToken(user, accessTokenExpiration, redirectUrl)
    }

    override fun generateRefreshToken(user: User): String {
        return generateToken(user, refreshTokenExpiration)
    }

    override fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun getUserIdFromToken(token: String): UUID? {
        return try {
            val claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .payload
            UUID.fromString(claims.subject)
        } catch (e: Exception) {
            null
        }
    }

    private fun generateToken(user: User, expirationMs: Long, redirectUrl: String? = null): String {
        val now = Date()
        val expiry = Date(now.time + expirationMs)

        val builder = Jwts.builder()
            .subject(user.id.toString())
            .claim("roles", user.roles)
            .claim("applicationId", user.applicationId.toString())
        
        if (redirectUrl != null) {
            builder.claim("redirectUrl", redirectUrl)
        }

        return builder
            .issuedAt(now)
            .expiration(expiry)
            .signWith(signingKey)
            .compact()
    }
}
