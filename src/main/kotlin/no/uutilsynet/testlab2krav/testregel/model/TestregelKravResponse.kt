package no.uutilsynet.testlab2krav.testregel.model

import no.uutilsynet.testlab2krav.dto.KravWcag2x

data class TestregelKravResponse(
    val id: Int,
    val testregelId: String,
    val namn: String,
    val krav: KravWcag2x,
)


