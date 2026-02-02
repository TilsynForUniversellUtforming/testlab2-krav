package no.uutilsynet.testlab2krav.testregel

import java.net.URI
import no.uutilsynet.testlab2.utils.ErrorHandlingUtil.createWithErrorHandling
import no.uutilsynet.testlab2.utils.ErrorHandlingUtil.executeWithErrorHandling
import no.uutilsynet.testlab2.validators.validateNamn
import no.uutilsynet.testlab2krav.dao.KravDAO
import no.uutilsynet.testlab2krav.testregel.import.TestregelImportService
import no.uutilsynet.testlab2krav.testregel.model.Testregel
import no.uutilsynet.testlab2krav.testregel.model.Testregel.Companion.validateTestregel
import no.uutilsynet.testlab2krav.testregel.model.TestregelAggregate
import no.uutilsynet.testlab2krav.testregel.model.TestregelInit
import no.uutilsynet.testlab2krav.testregel.model.TestregelKravResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private const val ERROR_GET_TESTREGLAR = "Feila ved henting av testreglar"

@RestController
@RequestMapping("v1/testreglar")
class TestregelResource(
  private val testregelImportService: TestregelImportService,
  private val testregelService: TestregelService,
  private val kravDAO: KravDAO
) {

  val logger = LoggerFactory.getLogger(TestregelResource::class.java)

  private val locationForId: (Int) -> URI = { id -> URI("/v1/testreglar/${id}") }

  @GetMapping("{id}")
  fun getTestregel(@PathVariable id: Int): ResponseEntity<Testregel> {
    return runCatching { testregelService.getTestregel(id) }
      .fold(
        onSuccess = { ResponseEntity.ok(it) }, onFailure = { ResponseEntity.notFound().build() })
  }

  @GetMapping("testregelKey/{testregelKey}")
  fun getTestregelByKey(@PathVariable testregelKey: String): ResponseEntity<TestregelKravResponse> {
    return runCatching {
        testregelService.getTestregelByKey(testregelKey).toTestregelKravResponse()
      }
      .fold(
        onSuccess = { ResponseEntity.ok(it) }, onFailure = { ResponseEntity.notFound().build() })
  }

  @GetMapping
  fun getTestregelListByIdList(
    @RequestParam testregelIdList: List<Int>?
  ): ResponseEntity<List<Testregel>> =
    runCatching {
        val testregelList = testregelService.getTestregelList()
        if (testregelIdList != null) {
          ResponseEntity.ok(testregelList.filter { testregelIdList.contains(it.id) })
        } else ResponseEntity.ok(testregelList)
      }
      .getOrElse {
        logger.error(ERROR_GET_TESTREGLAR, it)
        ResponseEntity.internalServerError().build()
      }

  @GetMapping("listTestregelKrav")
  fun getTestregelKravList(): ResponseEntity<List<TestregelKravResponse>> =
    runCatching {
        val testregelList = testregelService.getTestregelList().map { it.toTestregelKravResponse() }
        ResponseEntity.ok(testregelList)
      }
      .getOrElse {
        logger.error(ERROR_GET_TESTREGLAR, it)
        ResponseEntity.internalServerError().build()
      }

  @PostMapping
  fun createTestregel(@RequestBody testregelInit: TestregelInit): ResponseEntity<out Any> =
    createWithErrorHandling(
      {
        validateNamn(testregelInit.namn).getOrThrow()
        validateSchema(testregelInit.testregelSchema, testregelInit.modus).getOrThrow()
        validateKrav(testregelInit.kravId)

        testregelService.createTestregel(testregelInit)
      },
      locationForId)

  @PutMapping
  fun updateTestregel(@RequestBody testregel: Testregel): ResponseEntity<out Any> =
    executeWithErrorHandling {
      validateKrav(testregel.kravId)
      testregel.validateTestregel().getOrThrow()
      testregelService.updateTestregel(testregel)
    }

  @DeleteMapping("{testregelId}")
  fun deleteTestregel(@PathVariable("testregelId") testregelId: Int): ResponseEntity<out Any> =
    executeWithErrorHandling {
      testregelService.deleteTestregel(testregelId)
    }

  @GetMapping("innhaldstypeForTesting")
  fun getInnhaldstypeForTesting(): ResponseEntity<out Any> =
    runCatching { ResponseEntity.ok(testregelService.getInnhaldstypeForTesting()) }
      .getOrElse {
        logger.error("Feila ved henting av innhaldstype for testing", it)
        ResponseEntity.internalServerError().body(it.message)
      }

  @GetMapping("temaForTestreglar")
  fun getTemaForTesreglar(): ResponseEntity<out Any> =
    runCatching { ResponseEntity.ok(testregelService.getTemaForTestregel()) }
      .getOrElse {
        logger.error("Feila ved henting av tema for testreglar", it)
        ResponseEntity.internalServerError().body(it.message)
      }

  @GetMapping("testobjektForTestreglar")
  fun getTestobjektForTestreglar(): ResponseEntity<out Any> =
    runCatching { ResponseEntity.ok(testregelService.getTestobjekt()) }
      .getOrElse {
        logger.error("Feila ved henting av tema for testreglar", it)
        ResponseEntity.internalServerError().body(it.message)
      }

  @PostMapping("import")
  fun importTestreglar() {
    runCatching {
      val testreglarNett = testregelImportService.importTestreglarNett().getOrThrow()
      val testreglarApp = testregelImportService.importTestreglarApp().getOrThrow()
      logger.info("Importerte testreglar for nett: $testreglarNett  for app: $testreglarApp")
    }
  }

  fun validateKrav(kravId: Int) =
    runCatching { kravDAO.getWcagKrav(kravId) }
      .getOrElse { throw IllegalArgumentException("Krav med id $kravId finns ikkje") }

  @GetMapping("aggregates")
  fun getTestregelAggregates(): List<TestregelAggregate> {
    val testregelBaseList = testregelService.getTestregelList()
    val temaList = testregelService.getTemaForTestregel()
    val testobjektList = testregelService.getTestobjekt()
    val innhaldstypeTestingList = testregelService.getInnhaldstypeForTesting()
    val kravList = kravDAO.listWcagKrav()

    return testregelBaseList.map { testregel ->
      TestregelAggregate(
        id = testregel.id,
        namn = testregel.namn,
        tema = temaList.firstOrNull { it.id == testregel.tema },
        testobjekt = testobjektList.firstOrNull { it.id == testregel.testobjekt },
        innhaldstypeTesting =
          innhaldstypeTestingList.find { it.id == testregel.innhaldstypeTesting },
        krav = kravList.firstOrNull { it.id == testregel.kravId }
            ?: throw IllegalArgumentException("Krav med id ${testregel.kravId} finns ikkje"),
        modus = testregel.modus,
        testregelSchema = testregel.testregelSchema,
        testregelId = testregel.testregelId,
        versjon = testregel.versjon,
        status = testregel.status,
        datoSistEndra = testregel.datoSistEndra,
        type = testregel.type,
        spraak = testregel.spraak,
        kravTilSamsvar = testregel.kravTilSamsvar)
    }
  }

  @GetMapping("aggregates/{id}")
  fun getTestregelAggregate(@PathVariable id: Int): ResponseEntity<TestregelAggregate> {
    return runCatching {
        val testregel = testregelService.getTestregel(id)
        val tema =
          testregel.tema?.let {
            testregelService.getTemaForTestregel().firstOrNull { t -> t.id == it }
          }
        val testobjekt =
          testregel.testobjekt?.let {
            testregelService.getTestobjekt().firstOrNull { to -> to.id == it }
          }
        val innhaldstypeTesting =
          testregel.innhaldstypeTesting?.let {
            testregelService.getInnhaldstypeForTesting().firstOrNull {
              it.id == testregel.innhaldstypeTesting
            }
          }
        val krav = kravDAO.getWcagKrav(testregel.kravId)

        ResponseEntity.ok(
          TestregelAggregate(
            id = testregel.id,
            namn = testregel.namn,
            tema = tema,
            testobjekt = testobjekt,
            innhaldstypeTesting = innhaldstypeTesting,
            krav = krav,
            modus = testregel.modus,
            testregelSchema = testregel.testregelSchema,
            testregelId = testregel.testregelId,
            versjon = testregel.versjon,
            status = testregel.status,
            datoSistEndra = testregel.datoSistEndra,
            type = testregel.type,
            spraak = testregel.spraak,
            kravTilSamsvar = testregel.kravTilSamsvar))
      }
      .getOrElse {
        logger.error(ERROR_GET_TESTREGLAR, it)
        ResponseEntity.notFound().build()
      }
  }

  fun Testregel.toTestregelKravResponse(): TestregelKravResponse {
    val krav = kravDAO.getWcagKrav(kravId)
    return TestregelKravResponse(
      id = id,
      testregelId = testregelId,
      namn = namn,
      krav = krav,
    )
  }
}
