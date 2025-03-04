package no.uutilsynet.testlab2krav.krav

import no.uutilsynet.testlab2krav.dao.KravDAO
import no.uutilsynet.testlab2krav.dto.Krav
import no.uutilsynet.testlab2krav.dto.KravInit
import no.uutilsynet.testlab2krav.dto.KravWcag2x
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("v1/krav")
class KravResource(val kravDao: KravDAO) : KravApi {

  @GetMapping override fun listKrav(): List<Krav> = kravDao.listKrav()

  @GetMapping("wcag2krav") override fun listWcagKrav(): List<KravWcag2x> = kravDao.listWcagKrav()

  @GetMapping("wcag2krav/{id}")
  override fun getWcagKrav(@PathVariable id: Int): KravWcag2x = kravDao.getWcagKrav(id)

  @GetMapping("wcag2krav/suksesskriterium/{suksesskriterium}")
  override fun getKravBySuksesskriterium(@PathVariable suksesskriterium: String): KravWcag2x =
    kravDao.getKravBySuksesskriterium(suksesskriterium)

  @PostMapping("wcag2krav")
  override fun createWcagKrav(@RequestBody krav: KravInit): Int = kravDao.createWcagKrav(krav)

  @DeleteMapping override fun deleteKrav(kravId: Int): Boolean = kravDao.deleteKrav(kravId)

  @PutMapping("wcag2krav/{id}")
  override fun updateWcagKrav(
    @RequestBody krav: KravWcag2x,
    @PathVariable id: Int
  ): ResponseEntity<String> {
    require(krav.id == id) { "Krav id i path og body er ikkje like " + krav.id + " " + id }
    val status: Int = kravDao.updateWcagKrav(krav)
    if (status < 1) {
      return ResponseEntity.badRequest().body("Feil ved oppdatering av krav med id ${krav.id}")
    }
    return ResponseEntity.ok("Oppdatert krav med id ${krav.id}")
  }

  @DeleteMapping("wcag2krav/{id}")
  override fun deleteWcagKrav(@PathVariable id: Int): ResponseEntity<String> {
    val status: Boolean = kravDao.deleteKrav(id)
    if (!status) {
      return ResponseEntity.badRequest().body("Feil ved sletting av krav med id $id")
    }
    return ResponseEntity.ok("Slettet krav med id $id")
  }
}
