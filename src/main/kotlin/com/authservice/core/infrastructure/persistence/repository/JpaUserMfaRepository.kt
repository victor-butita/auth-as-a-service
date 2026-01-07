package com.authservice.core.infrastructure.persistence.repository

import com.authservice.core.domain.model.UserMfa
import com.authservice.core.domain.repository.UserMfaRepository
import com.authservice.core.infrastructure.persistence.entity.UserMfaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

interface UserMfaJpaRepository : JpaRepository<UserMfaEntity, UUID> {
    fun findByUserId(userId: UUID): Optional<UserMfaEntity>
    fun deleteByUserId(userId: UUID)
}

@Repository
class JpaUserMfaRepository(
    private val jpaRepository: UserMfaJpaRepository
) : UserMfaRepository {

    override fun findByUserId(userId: UUID): UserMfa? {
        return jpaRepository.findByUserId(userId).map { it.toDomain() }.orElse(null)
    }

    override fun save(userMfa: UserMfa): UserMfa {
        val entity = UserMfaEntity(
            id = userMfa.id,
            userId = userMfa.userId,
            secret = userMfa.secret,
            enabled = userMfa.enabled,
            backupCodes = userMfa.backupCodes
        )
        return jpaRepository.save(entity).toDomain()
    }

    override fun deleteByUserId(userId: UUID) {
        jpaRepository.deleteByUserId(userId)
    }

    private fun UserMfaEntity.toDomain() = UserMfa(
        id = this.id,
        userId = this.userId,
        secret = this.secret,
        enabled = this.enabled,
        backupCodes = this.backupCodes
    )
}
