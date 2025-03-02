package indi.gromov.ktor.requests

import io.ks3.java.math.BigDecimalAsString
import kotlinx.serialization.Serializable

@Serializable
data class CustomerCreateRequest(
    val fullName: String,
    val balance: BigDecimalAsString
)