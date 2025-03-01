package indi.gromov.db.customer

import indi.gromov.models.Customer
import indi.gromov.utils.converters.DaoToModelConverters
import indi.gromov.utils.transaction.suspendTransaction

class CustomerRepository {
    suspend fun allCustomers(): List<Customer> = suspendTransaction {
        CustomerDao.all().map(DaoToModelConverters::daoToModel)
    }

    suspend fun insertCustomer(customer: Customer) = suspendTransaction {
        CustomerDao.new {
            fullName = customer.fullName
            balance = customer.balance
        }
    }
}