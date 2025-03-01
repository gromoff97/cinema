package indi.gromov.models

import kotlinx.serialization.Serializable

@Serializable
data class Hall(
    val hallId: Int,
    val alternativeName: String
)