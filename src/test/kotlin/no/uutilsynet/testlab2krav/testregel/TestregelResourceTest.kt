package no.uutilsynet.testlab2krav.testregel

import java.net.URI
import java.time.Instant
import no.uutilsynet.testlab2.constants.*
import no.uutilsynet.testlab2krav.dao.KravDAO
import no.uutilsynet.testlab2krav.dto.KravWcag2x
import no.uutilsynet.testlab2krav.testregel.import.TestregelImportService
import no.uutilsynet.testlab2krav.testregel.model.InnhaldstypeTesting
import no.uutilsynet.testlab2krav.testregel.model.Tema
import no.uutilsynet.testlab2krav.testregel.model.Testobjekt
import no.uutilsynet.testlab2krav.testregel.model.Testregel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
import org.mockito.Mockito.mock

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestregelResourceTest {
  private val testregelService = mock<TestregelService>()
  private val kravDAO = mock<KravDAO>()
  private val testregelImportService = mock<TestregelImportService>()

  private val resource = TestregelResource(testregelImportService, testregelService, kravDAO)

  @Test
  fun `getTestregelAggregates returns correct aggregates`() {
    val testregelBase =
      Testregel(
        id = 1,
        namn = "Test",
        kravId = 5,
        modus = TestregelModus.manuell,
        testregelSchema = "schema",
        testregelId = "1.1.1",
        versjon = 1,
        status = TestregelStatus.publisert,
        datoSistEndra = Instant.now(),
        type = TestregelInnholdstype.nett,
        spraak = TestlabLocale.nb,
        kravTilSamsvar = "svar",
        tema = 2,
        testobjekt = 3,
        innhaldstypeTesting = 4)
    val tema = Tema(id = 2, tema = "Tema")
    val testobjekt = Testobjekt(id = 3, testobjekt = "Objekt")
    val innhaldstype = InnhaldstypeTesting(id = 4, innhaldstype = "Innhald")
    val krav =
      KravWcag2x(
        id = 5,
        tittel = "Krav",
        status = KravStatus.gjeldande,
        innhald = "Innhald",
        gjeldAutomat = true,
        gjeldNettsider = true,
        gjeldApp = false,
        urlRettleiing = URI("http://example.com").toURL(),
        prinsipp = WcagPrinsipp.robust,
        retningslinje = WcagRetninglinje.leselig,
        suksesskriterium = "Kriterium",
        samsvarsnivaa = WcagSamsvarsnivaa.AA,
        kommentarBrudd = "Kommentar")

    Mockito.`when`(testregelService.getTestregelList()).thenReturn(listOf(testregelBase))
    Mockito.`when`(testregelService.getTemaForTestregel()).thenReturn(listOf(tema))
    Mockito.`when`(testregelService.getTestobjekt()).thenReturn(listOf(testobjekt))
    Mockito.`when`(testregelService.getInnhaldstypeForTesting()).thenReturn(listOf(innhaldstype))
    Mockito.`when`(kravDAO.listKrav()).thenReturn(listOf(krav))

    val result = resource.getTestregelAggregates()

    assertEquals(1, result.size)
    val aggregate = result.first()
    assertEquals(testregelBase.id, aggregate.id)
    assertEquals(testregelBase.namn, aggregate.namn)
    assertEquals(tema, aggregate.tema)
    assertEquals(testobjekt, aggregate.testobjekt)
    assertEquals(innhaldstype, aggregate.innhaldstypeTesting)
    assertEquals(krav, aggregate.krav)
  }
}
