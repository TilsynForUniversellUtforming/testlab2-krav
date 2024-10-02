package no.uutilsynet.testlab2krav.dto

import java.net.URL
import no.uutilsynet.testlab2.constants.KravStatus

open class Krav(
    open val id: Int,
    open val tittel: String,
    open val status: KravStatus,
    open val innhald: String?,
    open val gjeldAutomat: Boolean,
    open val gjeldNettsider: Boolean,
    open val gjeldApp: Boolean,
    open val urlRettleiing: URL?,
)
