package no.uutilsynet.testlab2krav

import no.uutilsynet.testlab2krav.dao.KravDao
import no.uutilsynet.testlab2krav.dto.Krav
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DatabaseAccess(val kravDao: KravDao) {

  @GetMapping("/") fun getDatabaseData(): List<Krav> = kravDao.listKrav()
}
