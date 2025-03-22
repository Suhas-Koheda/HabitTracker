package dev.haas.routes

import dev.haas.models.UserLoginRequest
import dev.haas.models.UserRegisterRequest
import dev.haas.repositories.UserRespository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureAuthRouting() {
    routing {
        route("/auth/login") {
            post {
                try {
                    val loginRequest = call.receive<UserLoginRequest>()
                    val userRepo = UserRespository()
                    val user = userRepo.getAllUsers().find {
                        it.email == loginRequest.email && it.password == loginRequest.password
                    }
                    if (user != null) {
                        call.respond(HttpStatusCode.OK, "Login successful")
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Error: ${e.message}")
                }
            }
        }

        route("/auth/register") {
            post {
                println("here")
                try {
                    val registerRequest = call.receive<UserRegisterRequest>()
                    println(registerRequest)
                    val userRepo = UserRespository()
                    println("here")
                    userRepo.createUser(registerRequest)
                    println("here")
                    call.respond(HttpStatusCode.Created, "User registered successfully")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Error: ${e.message}")
                }
            }
        }
    }
}
