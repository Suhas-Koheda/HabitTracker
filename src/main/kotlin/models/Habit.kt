package dev.haas.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Habits: Table("habit") {
    val habitId = integer("habit_id").autoIncrement()
    val name = varchar("name", 255)
    val description = text("description")
    val targetDays = integer("target_days")
    val userId = integer("user_id").autoIncrement()
    override val primaryKey = PrimaryKey(habitId)
}

@Serializable
data class HabitRequest(val name: String, val description: String, val targetDays: Int)
data class HabitResponse(val habitId: MatchGroup?, val name: String, val description: String, val targetDays: MatchGroup?)