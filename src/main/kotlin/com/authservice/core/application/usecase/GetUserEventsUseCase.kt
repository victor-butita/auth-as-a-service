package com.authservice.core.application.usecase

import com.authservice.core.application.dto.EventDto
import com.authservice.core.domain.repository.EventRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class GetUserEventsUseCase(
    private val eventRepository: EventRepository
) {
    fun execute(userId: UUID): List<EventDto> {
        return eventRepository.findByUserId(userId).map {
            EventDto(
                id = it.id,
                type = it.type.name,
                timestamp = it.timestamp,
                ipAddress = it.ipAddress,
                userAgent = it.userAgent,
                metadata = it.metadata
            )
        }
    }
}
