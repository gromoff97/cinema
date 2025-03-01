package indi.gromov.db.customer

import indi.gromov.utils.converters.DaoToModelConverters
import indi.gromov.utils.transaction.suspendTransaction

class PostgresCustomerRepository : CustomerRepository {
    override suspend fun allCustomers() = suspendTransaction { CustomerDao.all().map(DaoToModelConverters::daoToModel) }
}