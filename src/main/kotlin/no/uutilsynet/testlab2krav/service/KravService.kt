package no.uutilsynet.testlab2krav.service

import no.uutilsynet.testlab2krav.dao.KravDao
import no.uutilsynet.testlab2krav.dto.Krav
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("krav")
class KravService(val kravDao: KravDao) {

    @GetMapping("all") fun listAll(): List<Krav> = kravDao.listKrav()

    @GetMapping fun findById(@RequestParam("id") id: Int) = kravDao.findById(id)
}
