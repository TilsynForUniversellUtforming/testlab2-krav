package no.uutilsynet.testlab2krav.dto

data class Krav(
    val id: Int,
    val tittel: String,
    val status: String,
    val innhald: String?,
    val gjeldautomat: Boolean,
    val gjeldnettsider: Boolean,
    val gjeldapp: Boolean,
    val urlrettleiing: String?
)
