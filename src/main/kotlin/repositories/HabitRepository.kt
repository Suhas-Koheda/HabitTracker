package dev.haas.repositories

import dev.haas.models.HabitDetailsResponse
import dev.haas.models.HabitRegisterRequest
import dev.haas.models.Habits
import dev.haas.models.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class HabitRepository {
    init {
        transaction {
            SchemaUtils.create(Habits)
            println("Habits table created successfully")
        }
    }
    fun getAllHabitsByUserId(userId: Int): List<HabitDetailsResponse> {
        return transaction {
            Habits.selectAll()
                .filter { it[Habits.userId] == userId }
                .map { habit ->
                    HabitDetailsResponse(
                        habitId = habit[Habits.habitId],
                        name = habit[Habits.name],
                        description = habit[Habits.description],
                        targetDays = habit[Habits.targetDays],
                        currentFollowDays = habit[Habits.currentFollowDays]
                    )
                }
        }
    }

    // Fetch details of a specific habit by habitId
    fun getHabitById(habitId: Int): HabitDetailsResponse? = transaction {
        Habits.selectAll()
            .filter { it[Habits.habitId] == habitId }
            .map { habit ->
                HabitDetailsResponse(
                    habitId = habit[Habits.habitId],
                    name = habit[Habits.name],
                    description = habit[Habits.description],
                    targetDays = habit[Habits.targetDays],
                    currentFollowDays = habit[Habits.currentFollowDays]
                )
            }
            .singleOrNull()
    }

    // Register a new habit
    fun registerHabit(userId: Int, habitRequest: HabitRegisterRequest): Int? = transaction {
        try {
            Habits.insert {
                it[name] = habitRequest.name
                it[description] = habitRequest.description
                it[targetDays] = habitRequest.targetDays
                it[Habits.userId] = habitRequest.userId
            }[Habits.habitId]
        } catch (e: Exception) {
            null // Handle errors (e.g., duplicate habit name or invalid userId)
        }
    }

    fun updateHabit(habitId: Int, habitRequest: HabitRegisterRequest): Boolean = transaction {
        // Fetch the current value of `currentFollowDays`
        val currentFollowDays = Habits.selectAll()
            .filter { it[Habits.habitId] == habitId }
            .map { it[Habits.currentFollowDays] }
            .singleOrNull() ?: 0

        // Update the habit, including incrementing `currentFollowDays`
        Habits.update({ Habits.habitId eq habitId }) {
            it[name] = habitRequest.name
            it[description] = habitRequest.description
            it[targetDays] = habitRequest.targetDays
            it[Habits.currentFollowDays] = currentFollowDays + 1 // Increment by 1
        } > 0
    }

    // Delete a habit
    fun deleteHabit(habitId: Int): Boolean = transaction {
        Habits.deleteWhere { Habits.habitId eq habitId } > 0
    }
}