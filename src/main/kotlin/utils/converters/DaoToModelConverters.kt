package indi.gromov.utils.converters

import indi.gromov.db.customer.CustomerDao
import indi.gromov.models.Customer

object DaoToModelConverters {
    fun daoToModel(dao: CustomerDao) = Customer(
        dao.fullName,
        dao.balance
    )
}