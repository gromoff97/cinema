package indi.gromov.models

import indi.gromov.utils.serializers.BigDecimalJson
import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val fullName: String,
    val balance: BigDecimalJson
)