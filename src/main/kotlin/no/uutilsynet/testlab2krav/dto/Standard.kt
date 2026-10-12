package no.uutilsynet.testlab2krav.dto

data class Standard(
    val id: Int,
    val tittel: String,
    val status: String,
    val gjeldOffentlig: Boolean,
    var gjeldPrivat: Boolean,
    val krav: List<Krav>
)
