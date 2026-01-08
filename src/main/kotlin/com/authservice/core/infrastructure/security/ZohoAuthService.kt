package com.authservice.core.infrastructure.security

import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.http.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

data class ZohoUser(
    val id: String,
    val email: String,
    val name: String? = null
)

@Service
class ZohoAuthService {
    private val restTemplate = RestTemplate()

    fun verifyCode(code: String, clientId: String, clientSecret: String, redirectUri: String): ZohoUser? {
        try {
            val url = "https://accounts.zoho.com/oauth/v2/token"
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

            val params: MultiValueMap<String, String> = LinkedMultiValueMap()
            params.add("code", code)
            params.add("client_id", clientId)
            params.add("client_secret", clientSecret)
            params.add("grant_type", "authorization_code")
            params.add("redirect_uri", redirectUri)

            val request = HttpEntity(params, headers)
            val response = restTemplate.postForEntity(url, request, Map::class.java)

            if (response.statusCode == HttpStatus.OK) {
                val body = response.body as Map<*, *>
                val accessToken = body["access_token"] as? String ?: return null
                return fetchUserInfo(accessToken)
            }
        } catch (e: Exception) {
            println("Zoho code verification failed: ${e.message}")
        }
        return null
    }

    private fun fetchUserInfo(accessToken: String): ZohoUser? {
        try {
            val url = "https://accounts.zoho.com/oauth/v2/userinfo"
            val headers = HttpHeaders()
            headers.setBearerAuth(accessToken)

            val request = HttpEntity<Unit>(headers)
            val response = restTemplate.exchange(url, HttpMethod.GET, request, Map::class.java)

            if (response.statusCode == HttpStatus.OK) {
                val body = response.body as Map<*, *>
                val id = body["sub"] as? String ?: body["user_id"] as? String ?: return null
                val email = body["email"] as? String ?: return null
                val name = body["name"] as? String
                return ZohoUser(id, email, name)
            }
        } catch (e: Exception) {
            println("Zoho userinfo fetch failed: ${e.message}")
        }
        return null
    }
}
