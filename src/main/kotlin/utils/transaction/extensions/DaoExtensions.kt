package indi.gromov.utils.transaction.extensions

import indi.gromov.db.BookingDao
import indi.gromov.db.CustomerDao
import indi.gromov.db.FilmDao
import indi.gromov.db.GenreDao
import indi.gromov.db.HallDao
import indi.gromov.db.SeatDao
import indi.gromov.db.SeatTypeDao
import indi.gromov.db.SessionDao
import indi.gromov.db.SessionSeatDao
import indi.gromov.models.Booking
import indi.gromov.models.Customer
import indi.gromov.models.Film
import indi.gromov.models.Genre
import indi.gromov.models.Hall
import indi.gromov.models.Seat
import indi.gromov.models.SeatType
import indi.gromov.models.Session
import indi.gromov.models.SessionSeat

fun BookingDao.toModel() = Booking(
    bookingId = id.value,
    bookingTime = bookingTime,
    sessionSeatId = sessionSeatId,
    customerId = customerId
)

fun CustomerDao.toModel() = Customer(
    customerId = id.value,
    fullName = fullName,
    balance = balance
)

fun FilmDao.toModel() = Film(
    filmId = id.value,
    name = name,
    genreId = genreId,
    duration = duration
)

fun GenreDao.toModel() = Genre(
    genreId = id.value,
    name = name
)

fun HallDao.toModel() = Hall(
    hallId = id.value,
    alternativeName = alternativeName
)

fun SeatDao.toModel() = Seat(
    seatId = id.value,
    hallId = hallId,
    rowNumber = rowNumber,
    seatNumber = seatNumber,
    seatTypeId = seatTypeId
)

fun SeatTypeDao.toModel() = SeatType(
    seatTypeId = id.value,
    name = name
)

fun SessionDao.toModel() = Session(
    sessionId = id.value,
    hallId = hallId,
    filmId = filmId,
    startTime = startTime
)

fun SessionSeatDao.toModel() = SessionSeat(
    seatSessionId = id.value,
    sessionId = sessionId,
    seatId = seatId,
    price = price
)