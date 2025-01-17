package no.uutilsynet.testlab2krav.dto

import java.net.URL
import no.uutilsynet.testlab2.constants.KravStatus
import no.uutilsynet.testlab2.constants.WcagPrinsipp
import no.uutilsynet.testlab2.constants.WcagRetninglinje
import no.uutilsynet.testlab2.constants.WcagSamsvarsnivaa

data class KravWcag2x(
    override val id: Int,
    override val tittel: String,
    override val status: KravStatus,
    override val innhald: String?,
    override val gjeldAutomat: Boolean,
    override val gjeldNettsider: Boolean,
    override val gjeldApp: Boolean,
    override val urlRettleiing: URL?,
    val prinsipp: WcagPrinsipp?,
    val retningslinje: WcagRetninglinje?,
    val suksesskriterium: String,
    val samsvarsnivaa: WcagSamsvarsnivaa?,
    override val kommentarBrudd: String?
) :
    Krav(
        id,
        tittel,
        status,
        innhald,
        gjeldAutomat,
        gjeldNettsider,
        gjeldApp,
        urlRettleiing,
        kommentarBrudd)
