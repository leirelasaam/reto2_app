package com.elorrieta.alumnoclient.entity
import java.time.LocalDateTime

enum class Status {
    ACEPTADA, RECHAZADA, PENDIENTE
}
data class Meeting(
    val id: Long, // BIGINT
    val userId: Long,
    val day: Int, // TINYINT
    val time: Int,
    val week: Int, // TINYINT
    val status: Status, // ENUM
    val createdAt: LocalDateTime, // TIMESTAMP
    val updatedAt: LocalDateTime
)
