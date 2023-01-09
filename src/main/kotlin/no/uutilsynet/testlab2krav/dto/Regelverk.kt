package no.uutilsynet.testlab2krav.dto

data class Regelverk(
    val id: Int,
    val namn: String,
    val type: String,
    val paragraf: String,
    val virkeomraader: String
)
