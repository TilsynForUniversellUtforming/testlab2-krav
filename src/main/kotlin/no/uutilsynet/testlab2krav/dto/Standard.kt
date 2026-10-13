package no.uutilsynet.testlab2krav.dto

import no.uutilsynet.testlab2.constants.KravStatus

data class Standard(
    val id: Int,
    val tittel: String,
    val status: KravStatus,
    val gjeldOffentlig: Boolean,
    var gjeldPrivat: Boolean,
    val krav: Set<Krav>,
)
