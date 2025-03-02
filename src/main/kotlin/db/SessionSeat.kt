package indi.gromov.db

import indi.gromov.ktor.requests.SessionSeatCreateRequest
import indi.gromov.models.SessionSeat
import indi.gromov.utils.transaction.extensions.toModel
import indi.gromov.utils.transaction.suspendTransaction
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

object SessionSeatTable : UUIDTable("session_seat", "seat_session_id") {
    val sessionId = uuid("session_id")
    val seatId = uuid("seat_id")
    val price = decimal("price", 10, 2)
}

class SessionSeatDao(id: EntityID<UUID>): UUIDEntity(id) {
    companion object: UUIDEntityClass<SessionSeatDao>(SessionSeatTable)

    var sessionId by SessionSeatTable.sessionId
    var seatId by SessionSeatTable.seatId
    var price by SessionSeatTable.price
}

class SessionSeatRepository {
    suspend fun allSessionSeats(): List<SessionSeat> = suspendTransaction {
        SessionSeatDao.all().map { it.toModel() }
    }

    suspend fun insertSessionSeat(sessionSeat: SessionSeatCreateRequest) = suspendTransaction {
        SessionSeatDao.new {
            sessionId = sessionSeat.sessionId
            seatId = sessionSeat.seatId
            price = sessionSeat.price
        }
    }
}