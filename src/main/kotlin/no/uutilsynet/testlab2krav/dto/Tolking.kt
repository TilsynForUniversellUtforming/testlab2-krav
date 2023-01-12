package no.uutilsynet.testlab2krav.dto

import java.time.LocalDateTime

data class Tolking(
    val id: Int,
    val idKrav: Int,
    val sistOppdatert: LocalDateTime,
    val innhald: String
)
