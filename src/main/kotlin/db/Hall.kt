package indi.gromov.db

import indi.gromov.ktor.requests.HallCreateRequest
import indi.gromov.models.Hall
import indi.gromov.utils.transaction.extensions.toModel
import indi.gromov.utils.transaction.suspendTransaction
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

// Таблица
object HallTable : IntIdTable("hall", "hall_id") {
    val alternativeName = text("alternative_name")
}

// DAO
class HallDao(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<HallDao>(HallTable)

    var alternativeName by HallTable.alternativeName
}

// Репозиторий
class HallRepository {
    suspend fun allHalls(): List<Hall> = suspendTransaction {
        HallDao.all().map { it.toModel() }
    }

    suspend fun insertHall(hall: HallCreateRequest) = suspendTransaction {
        HallDao.new {
            alternativeName = hall.alternativeName
        }
    }
}