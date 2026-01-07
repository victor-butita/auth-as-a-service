package com.authservice.core.web.controller

import com.authservice.core.application.dto.DashboardAnalyticsResponse
import com.authservice.core.application.usecase.GetAnalyticsUseCase
import com.authservice.core.application.usecase.GetUserEventsUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/analytics")
class AnalyticsController(
    private val getAnalyticsUseCase: GetAnalyticsUseCase,
    private val getUserEventsUseCase: GetUserEventsUseCase
) {

    @GetMapping("/events/user/{userId}")
    fun getUserEvents(@PathVariable userId: UUID): ResponseEntity<List<com.authservice.core.application.dto.EventDto>> {
        val response = getUserEventsUseCase.execute(userId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/application/{applicationId}")
    fun getApplicationAnalytics(@PathVariable applicationId: UUID): ResponseEntity<DashboardAnalyticsResponse> {
        val response = getAnalyticsUseCase.execute(applicationId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/debug/raw")
    fun getRawDebugData(): ResponseEntity<Map<String, Any>> {
        val data = getAnalyticsUseCase.getRawDebugInfo()
        return ResponseEntity.ok(data)
    }
}
