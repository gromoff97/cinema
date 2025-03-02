package indi.gromov.db

import indi.gromov.ktor.requests.SeatTypeCreateRequest
import indi.gromov.models.SeatType
import indi.gromov.utils.transaction.extensions.toModel
import indi.gromov.utils.transaction.suspendTransaction
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

// Таблица
object SeatTypeTable : UUIDTable("seat_type", "seat_type_id") {
    val name = text("name")
}

// DAO
class SeatTypeDao(id: EntityID<UUID>): UUIDEntity(id) {
    companion object: UUIDEntityClass<SeatTypeDao>(SeatTypeTable)

    var name by SeatTypeTable.name
}

// Репозиторий
object SeatTypeRepository {
    suspend fun allSeatTypes(): List<SeatType> = suspendTransaction {
        SeatTypeDao.all().map { it.toModel() }
    }

    suspend fun insertSeatType(seatType: SeatTypeCreateRequest) = suspendTransaction {
        SeatTypeDao.new {
            name = seatType.name
        }
    }
}