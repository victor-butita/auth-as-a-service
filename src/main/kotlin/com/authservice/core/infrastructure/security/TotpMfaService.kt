package com.authservice.core.infrastructure.security

import com.authservice.core.domain.service.MfaService
import dev.samstevens.totp.code.CodeVerifier
import dev.samstevens.totp.code.DefaultCodeGenerator
import dev.samstevens.totp.code.DefaultCodeVerifier
import dev.samstevens.totp.qr.QrData
import dev.samstevens.totp.qr.ZxingPngQrGenerator
import dev.samstevens.totp.secret.DefaultSecretGenerator
import dev.samstevens.totp.time.SystemTimeProvider
import org.springframework.stereotype.Service

@Service
class TotpMfaService : MfaService {
    private val secretGenerator = DefaultSecretGenerator()
    private val timeProvider = SystemTimeProvider()
    private val codeGenerator = DefaultCodeGenerator()
    private val codeVerifier = DefaultCodeVerifier(codeGenerator, timeProvider)

    override fun generateSecret(): String {
        return secretGenerator.generate()
    }

    override fun getQrCodeUrl(secret: String, email: String, issuer: String): String {
        val data = QrData.Builder()
            .label(email)
            .secret(secret)
            .issuer(issuer)
            .algorithm(dev.samstevens.totp.code.HashingAlgorithm.SHA1)
            .digits(6)
            .period(30)
            .build()
        // We return the content which can be used to generate a QR code URI
        return data.getUri()
    }

    override fun verifyCode(secret: String, code: String): Boolean {
        return codeVerifier.isValidCode(secret, code)
    }
}
