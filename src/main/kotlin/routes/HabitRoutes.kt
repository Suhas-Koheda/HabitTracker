package dev.haas.routes

import dev.haas.models.HabitRegisterRequest
import dev.haas.repositories.HabitRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureHabitRoutes() {
    val habitRepository = HabitRepository()

    routing {
        route("/habits") {
            get("/user/{userId}") {
                try {
                    val userId = call.parameters["userId"]?.toIntOrNull()
                    println("Received request for userId: $userId")
                    if (userId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
                        return@get
                    }
                    val habits = habitRepository.getAllHabitsByUserId(userId)
                    println("Fetched habits: $habits")
                    call.respond(HttpStatusCode.OK, habits)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error: ${e.message}")
                }
            }

            // Get a specific habit by ID
            get("/{habitId}") {
                try {
                    val habitId = call.parameters["habitId"]?.toIntOrNull()
                    if (habitId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid habit ID")
                        return@get
                    }
                    val habit = habitRepository.getHabitById(habitId)
                    if (habit == null) {
                        call.respond(HttpStatusCode.NotFound, "Habit not found")
                    } else {
                        call.respond(HttpStatusCode.OK, habit)
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error: ${e.message}")
                }
            }

            // Register a new habit
            post {
                try {
                    val habitRequest = call.receive<HabitRegisterRequest>()
                    val userId = habitRequest.userId // Assuming userId is part of the request
                    val habitId = habitRepository.registerHabit(userId, habitRequest)
                    if (habitId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Failed to register habit")
                    } else {
                        call.respond(HttpStatusCode.Created, mapOf("habitId" to habitId))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Error: ${e.message}")
                }
            }

            put("/{habitId}") {
                try {
                    val habitId = call.parameters["habitId"]?.toIntOrNull()
                    if (habitId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid habit ID")
                        return@put
                    }
                    val habitRequest = call.receive<HabitRegisterRequest>()
                    val updated = habitRepository.updateHabit(habitId, habitRequest)
                    if (updated) {
                        call.respond(HttpStatusCode.OK, "Habit updated successfully")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Habit not found")
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error: ${e.message}")
                }
            }

            // Delete a habit
            delete("/{habitId}") {
                try {
                    val habitId = call.parameters["habitId"]?.toIntOrNull()
                    if (habitId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid habit ID")
                        return@delete
                    }
                    val deleted = habitRepository.deleteHabit(habitId)
                    if (deleted) {
                        call.respond(HttpStatusCode.OK, "Habit deleted successfully")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Habit not found")
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error: ${e.message}")
                }
            }
        }
    }
}