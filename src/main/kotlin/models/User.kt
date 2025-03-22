package dev.haas.models;

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Users : Table("users") {
    val userId = integer("user_id").autoIncrement()
    val name = varchar("name", 255)
    val email = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 255)
    override val primaryKey = PrimaryKey(userId)
}

data class UserDetailsRequest(val userId: String,val habitsList: List<HabitResponse>)
@Serializable
data class UserRegisterRequest(val name: String, val email: String, val password: String)
@Serializable
data class UserLoginRequest(val email: String, val password: String)