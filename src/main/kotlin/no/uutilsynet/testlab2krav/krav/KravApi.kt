package no.uutilsynet.testlab2krav.krav

import no.uutilsynet.testlab2krav.dto.Krav

interface KravApi {
    fun listKrav(): List<Krav>
}
