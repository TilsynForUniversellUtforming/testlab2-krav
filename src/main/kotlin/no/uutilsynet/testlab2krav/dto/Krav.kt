package no.uutilsynet.testlab2krav.dto

data class Krav(
    val id: Int,
    val tittel: String,
    val status: String,
    val innhold: String?
)
