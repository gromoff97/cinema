package indi.gromov.models

import io.ks3.java.math.BigDecimalAsString
import io.ks3.java.typealiases.UuidAsString
import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val customerId: UuidAsString,
    val fullName: String,
    val balance: BigDecimalAsString
)