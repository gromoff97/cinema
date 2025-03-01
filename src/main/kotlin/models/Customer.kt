package indi.gromov.models

import io.ks3.java.math.BigDecimalAsString
import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val fullName: String,
    val balance: BigDecimalAsString
)