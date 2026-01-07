package com.authservice.core.infrastructure.persistence.repository

import com.authservice.core.infrastructure.persistence.entity.ApplicationEntity
import com.authservice.core.infrastructure.persistence.entity.TenantEntity
import com.authservice.core.infrastructure.persistence.entity.UserEntity
import com.authservice.core.infrastructure.persistence.entity.EventEntity
import com.authservice.core.domain.model.EventType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TenantJpaRepository : JpaRepository<TenantEntity, UUID>

@Repository
interface ApplicationJpaRepository : JpaRepository<ApplicationEntity, UUID> {
    fun findByClientId(clientId: String): ApplicationEntity?
    fun findByTenantId(tenantId: UUID): List<ApplicationEntity>
}

@Repository
interface UserJpaRepository : JpaRepository<UserEntity, UUID> {
    fun findByEmail(email: String): UserEntity?
    
    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.application.id = :applicationId")
    fun countByApplicationId(@Param("applicationId") applicationId: UUID): Long
    
    fun findByApplication_Id(applicationId: UUID): List<UserEntity>
}

@Repository
interface EventJpaRepository : JpaRepository<EventEntity, UUID> {
    @Query("SELECT e FROM EventEntity e WHERE e.applicationId = :applicationId ORDER BY e.timestamp DESC")
    fun findByApplicationIdOrderByTimestampDesc(@Param("applicationId") applicationId: UUID): List<EventEntity>
    
    @Query("SELECT COUNT(e) FROM EventEntity e WHERE e.applicationId = :applicationId AND e.type = :type")
    fun countByApplicationIdAndType(@Param("applicationId") applicationId: UUID, @Param("type") type: EventType): Long
    
    @Query("SELECT COUNT(e) FROM EventEntity e WHERE e.applicationId = :applicationId AND e.type = :type AND e.timestamp > :timestamp")
    fun countByApplicationIdAndTypeAndTimestampAfter(@Param("applicationId") applicationId: UUID, @Param("type") type: EventType, @Param("timestamp") timestamp: java.time.LocalDateTime): Long

    @Query("SELECT e FROM EventEntity e JOIN e.metadata m WHERE KEY(m) = 'userId' AND m = :userId ORDER BY e.timestamp DESC")
    fun findByUserIdMetadata(@Param("userId") userId: String): List<EventEntity>
}
