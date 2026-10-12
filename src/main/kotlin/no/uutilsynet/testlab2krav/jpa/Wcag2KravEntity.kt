package no.uutilsynet.testlab2krav.jpa

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.net.URL
import no.uutilsynet.testlab2.constants.KravStatus
import no.uutilsynet.testlab2.constants.WcagPrinsipp
import no.uutilsynet.testlab2.constants.WcagRetninglinje
import no.uutilsynet.testlab2.constants.WcagSamsvarsnivaa
import no.uutilsynet.testlab2krav.dto.Krav
import no.uutilsynet.testlab2krav.dto.Standard

@Suppress("LongParameterList")
@Entity(name = "wcag2krav")
class Wcag2KravEntity(
    @Column override var tittel: String,
    @Column override var status: KravStatus,
    @Column override var innhald: String?,
    @Column override var gjeldAutomat: Boolean,
    @Column override var gjeldNettsider: Boolean,
    @Column override var gjeldApp: Boolean,
    @Column override var urlRettleiing: URL?,
    @Column override var kommentarBrudd: String?,
    @Column var prinsipp: WcagPrinsipp?,
    @Column var retningslinje: WcagRetninglinje?,
    @Column var suksesskriterium: String,
    @Column var samsvarsnivaa: WcagSamsvarsnivaa?,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "standard_id")
    var standard: StandardEntity? = null,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null,
) : Krav
