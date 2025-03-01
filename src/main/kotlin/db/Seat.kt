package indi.gromov.db

import indi.gromov.models.Seat
import indi.gromov.utils.transaction.suspendTransaction
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

// Таблица
object SeatTable : UUIDTable("seat", "seat_id") {
    val hallId = integer("hall_id")
    val rowNumber = integer("row_number")
    val seatNumber = integer("seat_number")
    val seatTypeId = uuid("seat_type_id")

    init {
        uniqueIndex(hallId, rowNumber, seatNumber)
    }
}

// DAO
class SeatDao(id: EntityID<UUID>): UUIDEntity(id) {
    companion object: UUIDEntityClass<SeatDao>(SeatTable)

    var hallId by SeatTable.hallId
    var rowNumber by SeatTable.rowNumber
    var seatNumber by SeatTable.seatNumber
    var seatTypeId by SeatTable.seatTypeId
}

// Репозиторий
class SeatRepository {
    suspend fun allSeats(): List<Seat> = suspendTransaction {
        SeatDao.all().map { it.toModel() }
    }

    suspend fun insertSeat(seat: Seat) = suspendTransaction {
        SeatDao.new {
            hallId = seat.hallId
            rowNumber = seat.rowNumber
            seatNumber = seat.seatNumber
            seatTypeId = seat.seatTypeId
        }
    }
}

// Метод daoToModel
fun SeatDao.toModel(): Seat {
    return Seat(
        seatId = id.value,
        hallId = hallId,
        rowNumber = rowNumber,
        seatNumber = seatNumber,
        seatTypeId = seatTypeId
    )
}