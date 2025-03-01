package indi.gromov.db.customer

import org.jetbrains.exposed.dao.id.UUIDTable

object CustomerTable : UUIDTable("customer", "customer_id") {
    val fullName = text("full_name")
    val balance = decimal("balance", 10, 2)
}