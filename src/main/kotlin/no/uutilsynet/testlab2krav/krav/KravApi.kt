package no.uutilsynet.testlab2krav.krav

import no.uutilsynet.testlab2krav.dto.Krav
import no.uutilsynet.testlab2krav.dto.KravWcag2x
import org.springframework.http.ResponseEntity

interface KravApi {
    fun listKrav(): List<Krav>

    fun listWcagKrav(): List<KravWcag2x>

    fun getWcagKrav(id: Int): KravWcag2x

    fun getKravBySuksesskriterium(suksesskriterium: String): KravWcag2x

    fun createWcagKrav(krav: KravWcag2x): Int

    fun deleteKrav(kravId: Int): Boolean

    fun updateWcagKrav(krav: KravWcag2x): ResponseEntity<String>
}
