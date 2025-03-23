package dev.haas.routes

import dev.haas.models.UserLoginRequest
import dev.haas.models.UserRegisterRequest
import dev.haas.models.UserRequest
import dev.haas.models.Users
import dev.haas.repositories.UserRespository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import org.jetbrains.exposed.sql.update

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import java.util.*

fun Application.configureAuthRouting() {
    routing {
        route("/auth/register") {
            post {
                try {
                    val registerRequest = call.receive<UserRegisterRequest>()
                    val userRepo = UserRespository()
                    userRepo.createUser(registerRequest)
                    call.respond(HttpStatusCode.Created, "User registered successfully")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Error: ${e.message}")
                }
            }
        }

        route("/auth/login") {
            post {
                try {
                    val loginRequest = call.receive<UserLoginRequest>()
                    val userRepo = UserRespository()

                    // Find the user by email and compare plain-text passwords
                    val user = userRepo.getAllUsers().find { it.email == loginRequest.email }

                    if (user != null && user.password == loginRequest.password) {
                        // Generate a JWT
                        val jwt = generateJwt(user.userId, user.email)

                        // Update the user's JWT in the database
                        userRepo.updateUserJwt(user.userId, jwt)

                        // Return the JWT and the username
                        call.respond(HttpStatusCode.OK, mapOf("jwt" to jwt, "userId" to (user.userId).toString()))
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Error: ${e.message}")
                }
            }
        }

        route("/auth/logout") {
            post {
                try {
                    val logoutRequest = call.receive<UserRequest>()
                    val userRepo = UserRespository()

                    val user = userRepo.getAllUsers().find { it.email == logoutRequest.email }

                    if (user != null) {
                        // Clear the JWT for the user
                        userRepo.updateUserJwt(user.userId, "")
                        call.respond(HttpStatusCode.OK, "Logout successful")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "User not found")
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Error: ${e.message}")
                }
            }
        }
        route("/auth/validate") {
            post {
                try {
                    // Receive the JWT from the request body
                    val request = call.receive<Map<String, String>>()
                    val jwt = request["jwt"] ?: throw IllegalArgumentException("JWT is missing")

                    // Validate the JWT
                    val decodedJwt = validateJwt(jwt)

                    if (decodedJwt != null) {
                        // JWT is valid
                        call.respond(HttpStatusCode.OK, mapOf("valid" to true, "userId" to decodedJwt.subject))
                    } else {
                        // JWT is invalid
                        call.respond(HttpStatusCode.Unauthorized, mapOf("valid" to false, "message" to "Invalid JWT"))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Error: ${e.message}")
                }
            }
        }
    }
}

// Function to generate JWT
fun generateJwt(userId: Int, email: String): String {
    val algorithm = Algorithm.HMAC256("your-secret-key") // Replace with your secret key
    return JWT.create()
        .withIssuer("your-issuer") // Replace with your issuer
        .withSubject(userId.toString())
        .withClaim("email", email)
        .withExpiresAt(Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // 1 week expiration
        .sign(algorithm)
}
// Function to validate JWT
fun validateJwt(token: String): DecodedJWT? {
    return try {
        val algorithm = Algorithm.HMAC256("your-secret-key") // Use the same secret key as in generateJwt
        val verifier = JWT.require(algorithm)
            .withIssuer("your-issuer") // Use the same issuer as in generateJwt
            .build()
        verifier.verify(token) // Verify the token
    } catch (e: JWTVerificationException) {
        null // Return null if the token is invalid
    }
}
