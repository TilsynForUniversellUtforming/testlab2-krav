package no.uutilsynet.testlab2krav.jpa

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import java.net.URL
import no.uutilsynet.testlab2.constants.KravStatus
import no.uutilsynet.testlab2.constants.WcagPrinsipp
import no.uutilsynet.testlab2.constants.WcagRetninglinje
import no.uutilsynet.testlab2.constants.WcagSamsvarsnivaa
import no.uutilsynet.testlab2krav.dto.Krav
import org.hibernate.annotations.ColumnDefault

@Suppress("LongParameterList")
@Entity(name = "wcag2krav")
class Wcag2KravEntity(
    @Column override var tittel: String,

    @Convert(converter = KravStatusConverter::class)
    override var status: KravStatus,

    override var innhald: String?,

    @Column(name = "gjeldautomat")
    override var gjeldAutomat: Boolean,

    @Column(name = "gjeldnettsider")
    override var gjeldNettsider: Boolean,

    @Column(name = "gjeldapp")
    override var gjeldApp: Boolean,

    @Column(name = "urlrettleiing")
    override var urlRettleiing: URL?,

    @Column(name = "kommentar_brudd")
    override var kommentarBrudd: String?,

    @Convert(converter = WcagPrinsippConverter::class)
    var prinsipp: WcagPrinsipp?,

    @Convert(converter = WcagRetninglinjeConverter::class)
    var retningslinje: WcagRetninglinje?,

    var suksesskriterium: String,

    @Convert(converter = WcagSamsvarsnivaaConverter::class)
    @Column(name = "samsvarsnivaa")
    var samsvarsnivaa: WcagSamsvarsnivaa?,

    @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(
        name = "standard_krav",
        joinColumns = [JoinColumn(name = "krav_id")],
        inverseJoinColumns = [JoinColumn(name = "standard_id")]
    )
    var standard: MutableSet<StandardEntity> = mutableSetOf(),

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null,
) : Krav
