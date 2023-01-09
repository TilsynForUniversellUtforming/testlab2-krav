package no.uutilsynet.testlab2krav.dto

import java.time.LocalDate

data class Tolking(
    val id: Int,
    val idKrav: Int,
    val sistOppdatert: LocalDate,
    val innhald: String
)
