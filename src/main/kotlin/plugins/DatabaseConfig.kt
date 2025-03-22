import io.ktor.server.application.Application
import io.ktor.server.application.log
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabase() {
    val url = "jdbc:postgresql://ep-young-rain-a5wyzmtd-pooler.us-east-2.aws.neon.tech/neondb"
    val user = "neondb_owner"
    val password = "npg_TlZx0DHCwGP3"

    log.info("Connecting to Postgres database at $url")
    Database.connect(url, driver = "org.postgresql.Driver", user = user, password = password)
}
