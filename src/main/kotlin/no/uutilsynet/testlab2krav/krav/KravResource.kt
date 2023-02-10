package no.uutilsynet.testlab2krav.krav

import no.uutilsynet.testlab2krav.dao.KravDAO
import no.uutilsynet.testlab2krav.dto.Krav
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/krav")
class KravResource(val kravDao: KravDAO) : KravApi {

    @GetMapping override fun listKrav(): List<Krav> = kravDao.listKrav()
}
