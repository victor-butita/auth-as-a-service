package com.authservice.core.infrastructure.persistence.repository

import com.authservice.core.domain.model.User
import com.authservice.core.domain.repository.UserRepository
import com.authservice.core.infrastructure.persistence.entity.UserEntity
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class JpaUserRepository(
    private val userJpaRepository: UserJpaRepository,
    private val applicationJpaRepository: ApplicationJpaRepository
) : UserRepository {

    override fun findById(id: UUID): User? {
        return userJpaRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findByEmail(email: String): User? {
        return userJpaRepository.findByEmail(email)?.toDomain()
    }

    override fun findByApplicationId(applicationId: UUID): List<User> {
        return userJpaRepository.findByApplication_Id(applicationId).map { it.toDomain() }
    }

    override fun countByApplicationId(applicationId: UUID): Long {
        return userJpaRepository.countByApplicationId(applicationId)
    }

    override fun save(user: User): User {
        val application = applicationJpaRepository.findById(user.applicationId)
            .orElseThrow { IllegalArgumentException("Application not found") }
        
        val entity = UserEntity(
            id = user.id,
            application = application,
            email = user.email,
            passwordHash = user.passwordHash,
            roles = user.roles.toMutableSet(),
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
        return userJpaRepository.save(entity).toDomain()
    }

    override fun deleteById(id: UUID) {
        userJpaRepository.deleteById(id)
    }

    private fun UserEntity.toDomain() = User(
        id = this.id,
        applicationId = this.application.id,
        email = this.email,
        passwordHash = this.passwordHash,
        roles = this.roles,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
