package com.authservice.core.infrastructure.security

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import org.springframework.stereotype.Service
import java.util.Collections

@Service
class GoogleAuthService {
    private val transport = NetHttpTransport()
    private val jsonFactory = GsonFactory.getDefaultInstance()

    fun verifyToken(idTokenString: String, clientId: String): GoogleIdToken? {
        val verifier = GoogleIdTokenVerifier.Builder(transport, jsonFactory)
            .setAudience(Collections.singletonList(clientId))
            .build()

        return try {
            verifier.verify(idTokenString)
        } catch (e: Exception) {
            println("Google token verification failed: ${e.message}")
            null
        }
    }
}
