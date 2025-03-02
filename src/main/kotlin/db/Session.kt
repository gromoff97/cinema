package indi.gromov.db

import indi.gromov.ktor.requests.SessionCreateRequest
import indi.gromov.models.Session
import indi.gromov.utils.transaction.extensions.toModel
import indi.gromov.utils.transaction.suspendTransaction
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import java.util.UUID

// Таблица
object SessionTable : UUIDTable("session", "session_id") {
    val hallId = integer("hall_id")
    val filmId = uuid("film_id")
    val startTime = timestamp("start_time")
}

// DAO
class SessionDao(id: EntityID<UUID>): UUIDEntity(id) {
    companion object: UUIDEntityClass<SessionDao>(SessionTable)

    var hallId by SessionTable.hallId
    var filmId by SessionTable.filmId
    var startTime by SessionTable.startTime
}

// Репозиторий
class SessionRepository {
    suspend fun allSessions(): List<Session> = suspendTransaction {
        SessionDao.all().map { it.toModel() }
    }

    suspend fun insertSession(session: SessionCreateRequest) = suspendTransaction {
        SessionDao.new {
            hallId = session.hallId
            filmId = session.filmId
            startTime = session.startTime
        }
    }
}