package dev.haas.models

import org.jetbrains.exposed.sql.Table

object Habits : Table("habit") {
    val habitId = integer("habit_id").autoIncrement()
    val name = varchar("name", 255)
    val description = text("description")
    val targetDays = integer("target_days")
    val currentFollowDays = integer("current_follow_days").default(0)
    val userId = integer("user_id").references(Users.userId)
    override val primaryKey = PrimaryKey(habitId)
}

@kotlinx.serialization.Serializable
data class HabitDetailsRequest(val habitId: Int, val description: String, val targetDays: Int)

@kotlinx.serialization.Serializable
data class HabitRegisterRequest(val name: String, val description: String, val targetDays: Int, val userId: Int)

@kotlinx.serialization.Serializable
data class HabitDetailsResponse(val habitId: Int, val name: String, val description: String, val targetDays: Int, val currentFollowDays: Int)