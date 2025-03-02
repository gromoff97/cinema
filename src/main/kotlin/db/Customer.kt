package indi.gromov.db

import indi.gromov.ktor.requests.CustomerCreateRequest
import indi.gromov.models.Customer
import indi.gromov.utils.transaction.extensions.toModel
import indi.gromov.utils.transaction.suspendTransaction
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

object CustomerTable : UUIDTable("customer", "customer_id") {
    val fullName = text("full_name")
    val balance = decimal("balance", 10, 2)
}

class CustomerDao(id: EntityID<UUID>): UUIDEntity(id) {
    companion object: UUIDEntityClass<CustomerDao>(CustomerTable)

    var fullName by CustomerTable.fullName
    var balance by CustomerTable.balance
}

class CustomerRepository {
    suspend fun allCustomers(): List<Customer> = suspendTransaction {
        CustomerDao.all().map { it.toModel() }
    }

    suspend fun insertCustomer(customer: CustomerCreateRequest) = suspendTransaction {
        CustomerDao.new {
            fullName = customer.fullName
            balance = customer.balance
        }
    }
}