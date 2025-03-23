package dev.haas.models;

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Users : Table("users") {
    val userId = integer("user_id").autoIncrement()
    val name = varchar("name", 255)
    val email = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 255)
    val jwt=varchar("jwt", 255).default("")
    val fieryDays= integer("fiery_days").default(0)
    override val primaryKey = PrimaryKey(userId)
}
@Serializable
data class UserRegisterRequest(val name: String, val email: String, val password: String)
@Serializable
data class UserLoginRequest(val email: String, val password: String)
@Serializable
data class UserRequest(val email: String, val jwt: String)