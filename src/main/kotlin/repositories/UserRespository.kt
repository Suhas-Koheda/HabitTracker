package dev.haas.repositories

import dev.haas.models.UserRegisterRequest
import dev.haas.models.Users
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


data class User(val userId: Int, val name: String, val email: String,val password: String)
class UserRespository{
    init{
        transaction {
            SchemaUtils.create(Users)
        }
    }

    fun createUser(user: UserRegisterRequest){
        transaction {
            Users.insert {
                it[email] = user.email
                it[name] = user.name
                it[password] = user.password
            }[Users.userId]
        }
    }

    fun getAllUsers(): List<User>{
        return transaction {
            Users.selectAll().map{
                User(it[Users.userId], it[Users.name], it[Users.email], it[Users.password])
            }
        }
    }

    fun getUserById(id: Int): User? = transaction {
        Users.select(Users.userId eq id).map {
            User(it[Users.userId], it[Users.name], it[Users.email], it[Users.password])
        }.singleOrNull()
    }

    // Update a user
    fun updateUser(id: Int, name: String, email: String): Boolean = transaction {
        Users.update({ Users.userId eq id }) {
            it[Users.name] = name
            it[Users.email] = email
        } > 0
    }

    fun deleteUser(id: Int): Boolean = transaction {
        Users.deleteWhere { Users.userId eq id } > 0
    }
}
