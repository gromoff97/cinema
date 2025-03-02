package indi.gromov.db

import indi.gromov.ktor.requests.BookingCreateRequest
import indi.gromov.models.Booking
import indi.gromov.utils.transaction.extensions.toModel
import indi.gromov.utils.transaction.suspendTransaction
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import java.util.UUID

object BookingTable : UUIDTable("booking", "booking_id") {
    val bookingTime = timestamp("booking_time")
    val sessionSeatId = uuid("session_seat_id")
    val customerId = uuid("customer_id")
}

class BookingDao(id: EntityID<UUID>): UUIDEntity(id) {
    companion object: UUIDEntityClass<BookingDao>(BookingTable)

    var bookingTime by BookingTable.bookingTime
    var sessionSeatId by BookingTable.sessionSeatId
    var customerId by BookingTable.customerId
}

class BookingRepository {
    suspend fun allBookings(): List<Booking> = suspendTransaction {
        BookingDao.all().map { it.toModel() }
    }

    suspend fun insertBooking(booking: BookingCreateRequest) = suspendTransaction {
        BookingDao.new {
            bookingTime = booking.bookingTime
            sessionSeatId = booking.sessionSeatId
            customerId = booking.customerId
        }
    }
}