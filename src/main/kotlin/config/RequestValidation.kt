package indi.gromov.config

import indi.gromov.db.BookingRepository
import indi.gromov.db.CustomerRepository
import indi.gromov.db.FilmRepository
import indi.gromov.db.SessionRepository
import indi.gromov.db.SessionSeatRepository
import indi.gromov.ktor.requests.BookingCreateRequest
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult.Invalid
import io.ktor.server.plugins.requestvalidation.ValidationResult.Valid

fun Application.configureRequestValidation() {
    install(RequestValidation) {
        validate<BookingCreateRequest> { req ->
            val sessionSeat = SessionSeatRepository.allSessionSeats().find { it.seatSessionId == req.sessionSeatId }
            if (sessionSeat == null) {
                return@validate Invalid("Не была найдена связка 'место-сеанс' (${req.sessionSeatId})")
            }

            val customer = CustomerRepository.allCustomers().find { it.customerId == req.customerId }
            if (customer == null) {
                return@validate Invalid("Не был найден пользователь (${req.customerId})")
            }

            val alreadyHasBooking = BookingRepository.allBookings().any { it.sessionSeatId == req.sessionSeatId }
            if (alreadyHasBooking) {
                return@validate Invalid("Такая бронь уже была создана")
            }

            if (sessionSeat.price > customer.balance) {
                return@validate Invalid("У пользователя '${customer.fullName}' недостаточно средств")
            }

            val session = SessionRepository.allSessions().find { it.sessionId == sessionSeat.sessionId }
            if (session == null) {
                throw AssertionError("Не нашлась сессия. Это сообщение значит, что нарушена консистентность БД")
            }

            val film = FilmRepository.allFilms().find { it.filmId == session.filmId }
            if (film == null) {
                throw AssertionError("Не нашёлся фильм. Это сообщение значит, что нарушена консистентность БД")
            }

            val filmEndTime = session.startTime + film.duration
            if (filmEndTime < req.bookingTime) {
                return@validate Invalid("Нельзя записаться на сеанс, когда время бронирования позже окончания сеанса")
            }

            return@validate Valid
        }
    }
}