package com.authservice.core.application.dto

import java.util.UUID

data class UpdateUserRolesRequest(
    val userId: UUID,
    val roles: Set<String>
)
