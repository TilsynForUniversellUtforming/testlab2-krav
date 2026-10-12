package no.uutilsynet.testlab2krav.krav

import no.uutilsynet.testlab2krav.dao.KravDAO
import no.uutilsynet.testlab2krav.dto.Krav
import no.uutilsynet.testlab2krav.dto.KravInit
import no.uutilsynet.testlab2krav.dto.KravWcag2x
import no.uutilsynet.testlab2krav.service.KravService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/krav")
class KravResource(val kravDao: KravDAO,val kravService: KravService)  {

  @GetMapping
  fun listKrav(): List<Krav> = kravService.listWcagKrav()

  @GetMapping("wcag2krav") fun listWcagKrav(): List<KravWcag2x> = kravService.listWcagKrav()

  @GetMapping("wcag2krav/{id}")
  fun getWcagKrav(@PathVariable id: Int): ResponseEntity<KravWcag2x> {
    return kravService.getWcagKrav(id).fold(
        onSuccess = { ResponseEntity.ok(it) },
        onFailure = { ResponseEntity.notFound().build() }
    )
  }

  @GetMapping("wcag2krav/suksesskriterium/{suksesskriterium}")
  fun getKravBySuksesskriterium(@PathVariable suksesskriterium: String): ResponseEntity<KravWcag2x> =
      kravService.getKravBySuksesskriterium(suksesskriterium).fold(
          onSuccess = { ResponseEntity.ok(it) },
          onFailure = { ResponseEntity.notFound().build() }
      )

  @PostMapping("wcag2krav")
  fun createWcagKrav(@RequestBody krav: KravInit): Int = kravService.createWcagKrav(krav)

  @DeleteMapping
  fun deleteKrav(kravId: Int): Boolean = kravService.deleteKrav(kravId)

  @PutMapping("wcag2krav/{id}")
  fun updateWcagKrav(
      @RequestBody krav: KravWcag2x,
      @PathVariable id: Int,
  ): ResponseEntity<String> {
    require(krav.id == id) { "Krav id i path og body er ikkje like " + krav.id + " " + id }
    val status: Int = kravDao.updateWcagKrav(krav)
    if (status < 1) {
      return ResponseEntity.badRequest().body("Feil ved oppdatering av krav med id ${krav.id}")
    }
    return ResponseEntity.ok("Oppdatert krav med id ${krav.id}")
  }

  @DeleteMapping("wcag2krav/{id}")
  fun deleteWcagKrav(@PathVariable id: Int): ResponseEntity<String> {
    val status: Boolean = kravDao.deleteKrav(id)
    if (!status) {
      return ResponseEntity.badRequest().body("Feil ved sletting av krav med id $id")
    }
    return ResponseEntity.ok("Slettet krav med id $id")
  }
}
