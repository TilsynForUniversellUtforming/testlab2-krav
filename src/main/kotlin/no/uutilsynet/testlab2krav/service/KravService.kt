package no.uutilsynet.testlab2krav.service

import no.uutilsynet.testlab2krav.dto.Krav
import no.uutilsynet.testlab2krav.dto.KravInit
import no.uutilsynet.testlab2krav.dto.KravWcag2x
import no.uutilsynet.testlab2krav.dto.Standard
import no.uutilsynet.testlab2krav.jpa.StandardEntity
import no.uutilsynet.testlab2krav.jpa.StandardRepository
import no.uutilsynet.testlab2krav.jpa.Wcag2KravEntity
import no.uutilsynet.testlab2krav.jpa.Wcag2KravRepository
import no.uutilsynet.testlab2krav.krav.KravApi
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class KravService(val wcag2KravRepository: Wcag2KravRepository, val standardRepository: StandardRepository) : KravApi {
    override fun listKrav(): List<Krav> {
        TODO("Not yet implemented")
    }


    override fun listWcagKrav(): List<KravWcag2x> {
        return wcag2KravRepository.findAll().map { it.toKravWcag2x() }
    }

    override fun getWcagKrav(id: Int): Result<KravWcag2x> {
        val kravEntity = wcag2KravRepository.findById(id).orElse(null)
        return if (kravEntity != null) {
            Result.success(kravEntity.toKravWcag2x())
        } else {
            Result.failure(NoSuchElementException("Krav with id $id not found"))
        }
    }

    override fun getKravBySuksesskriterium(suksesskriterium: String): Result<KravWcag2x> {
        return runCatching {
            wcag2KravRepository.findBySuksesskriterium(suksesskriterium).toKravWcag2x()
        }
    }

    fun getKravForStandard(standardId: Int): Result<List<KravWcag2x>> {
        val standard = standardRepository.findById(standardId)
        return if (standard != null) {
            val kravList = wcag2KravRepository.findByStandard(standard)
                .map { kravEntity ->
                    kravEntity.toKravWcag2x()
                }
            Result.success(kravList)
        } else {
            Result.failure(NoSuchElementException("Standard not found"))

        }
    }

    override fun createWcagKrav(krav: KravInit): Int {
        var standard: StandardEntity? = null
        if (krav.standardId != null) {
            standard = standardRepository.findById(krav.standardId)
        }
        val kravEntity = Wcag2KravEntity(
            tittel = krav.tittel,
            status = krav.status,
            innhald = krav.innhald,
            gjeldAutomat = krav.gjeldAutomat,
            gjeldNettsider = krav.gjeldNettsider,
            gjeldApp = krav.gjeldApp,
            urlRettleiing = krav.urlRettleiing,
            prinsipp = krav.prinsipp,
            retningslinje = krav.retningslinje,
            suksesskriterium = krav.suksesskriterium,
            samsvarsnivaa = krav.samsvarsnivaa,
            kommentarBrudd = krav.kommentarBrudd,
            standard = standard
        )
        val savedKravEntity = wcag2KravRepository.save(kravEntity)
        return savedKravEntity.id!!
    }

    fun Wcag2KravEntity.toKravWcag2x(): KravWcag2x = KravWcag2x(
        id = this.id!!,
        tittel = this.tittel,
        status = this.status,
        innhald = this.innhald,
        gjeldAutomat = this.gjeldAutomat,
        gjeldNettsider = this.gjeldNettsider,
        gjeldApp = this.gjeldApp,
        urlRettleiing = this.urlRettleiing,
        prinsipp = this.prinsipp,
        retningslinje = this.retningslinje,
        suksesskriterium = this.suksesskriterium,
        samsvarsnivaa = this.samsvarsnivaa,
        kommentarBrudd = this.kommentarBrudd,
        standard = this.standard?.toStandard()
    )

    override fun deleteKrav(kravid: Int): Boolean {
        return if (wcag2KravRepository.existsById(kravid)) {
            wcag2KravRepository.deleteById(kravid)
            true
        } else {
            false
        }
    }

    override fun updateWcagKrav(
        krav: KravWcag2x,
        id: Int
    ): ResponseEntity<String> {
        TODO("Not yet implemented")
    }

    override fun deleteWcagKrav(id: Int): ResponseEntity<String> {
        TODO("Not yet implemented")
    }

    fun updateWcagKrav(krav: KravWcag2x): Int {
        val updatedKrav = wcag2KravRepository.save(krav.toWcag2KravEntity())
        return updatedKrav.id!!
    }

    fun KravWcag2x.toWcag2KravEntity(): Wcag2KravEntity {
        return Wcag2KravEntity(
            tittel = this.tittel,
            status = this.status,
            innhald = this.innhald,
            gjeldAutomat = this.gjeldAutomat,
            gjeldNettsider = this.gjeldNettsider,
            gjeldApp = this.gjeldApp,
            urlRettleiing = this.urlRettleiing,
            prinsipp = this.prinsipp,
            retningslinje = this.retningslinje,
            suksesskriterium = this.suksesskriterium,
            samsvarsnivaa = this.samsvarsnivaa,
            kommentarBrudd = this.kommentarBrudd,
            standard = this.standard?.toStandardEntity()
        )
    }

    fun StandardEntity.toStandard(): Standard {
        return Standard(
        this.id!!,
        this.tittel,
        this.status,
        gjeldOffentlig = this.gjeldOffentlig,
        gjeldPrivat = this.gjeldPrivat,
        krav = this.krav.map { it.toKravWcag2x() })
    }

    fun Standard.toStandardEntity(): StandardEntity = StandardEntity(
        tittel = this.tittel,
        status = this.status,
        id = this.id,
        gjeldOffentlig = this.gjeldOffentlig,
        gjeldPrivat = this.gjeldPrivat,
        krav = this.krav.map { it as KravWcag2x }.map { it.toWcag2KravEntity() } as MutableList<Wcag2KravEntity>
    )
}