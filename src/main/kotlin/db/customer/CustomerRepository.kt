package indi.gromov.db.customer

import indi.gromov.models.Customer

interface CustomerRepository {
    suspend fun allCustomers(): List<Customer>
}