package indi.gromov.db.customer

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class CustomerDao(id: EntityID<UUID>): UUIDEntity(id) {
    companion object: UUIDEntityClass<CustomerDao>(CustomerTable)

    var fullName by CustomerTable.fullName
    var balance by CustomerTable.balance
}