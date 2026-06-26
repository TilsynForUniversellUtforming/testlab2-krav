package no.uutilsynet.testlab2krav.testregel

import java.time.Instant
import no.uutilsynet.testlab2.constants.TestlabLocale
import no.uutilsynet.testlab2.constants.TestregelInnholdstype
import no.uutilsynet.testlab2.constants.TestregelModus
import no.uutilsynet.testlab2.constants.TestregelStatus
import no.uutilsynet.testlab2.constants.TestresultatUtfall
import no.uutilsynet.testlab2krav.dao.KravDAO
import no.uutilsynet.testlab2krav.testregel.import.TestregelImportService
import no.uutilsynet.testlab2krav.testregel.model.ManuellForenklaTestregelDefinition
import no.uutilsynet.testlab2krav.testregel.model.Testregel
import no.uutilsynet.testlab2krav.testregel.model.TestregelUtfall
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(TestregelResource::class)
@Import(TestregelResourceSerializationTest.WebMvcTestConfig::class)
class TestregelResourceSerializationTest {

  @Autowired private lateinit var mockMvc: MockMvc

  @MockitoBean lateinit var testregelService: TestregelService
  @MockitoBean lateinit var kravDAO: KravDAO
  @MockitoBean lateinit var testregelImportService: TestregelImportService

  @TestConfiguration
  class WebMvcTestConfig {
    @Bean fun restTemplateBuilder(): RestTemplateBuilder = RestTemplateBuilder()
  }

  @Test
  fun `getTestregel returns serialized manuellForenkla definition`() {
    val testregel =
      Testregel(
        id = 42,
        testregelId = "MANUELL-FORENKLA-42",
        versjon = 1,
        namn = "Manuell forenkla",
        kravId = 3,
        status = TestregelStatus.publisert,
        datoSistEndra = Instant.parse("2026-06-01T12:00:00Z"),
        type = TestregelInnholdstype.nett,
        modus = TestregelModus.manuellForenkla,
        spraak = TestlabLocale.nb,
        tema = 1,
        testobjekt = 2,
        kravTilSamsvar = "samsvar",
        testregelSchema = "{\"utfall\":[]}",
        innhaldstypeTesting = 4,
        definition =
          ManuellForenklaTestregelDefinition(
            description = "Forenkla definisjon",
            utfall =
              listOf(
                TestregelUtfall(
                  id = 1,
                  beskrivelse = "Alt ok",
                  testresultat = TestresultatUtfall.samsvar,
                  default = true))))

    Mockito.`when`(testregelService.getTestregel(42)).thenReturn(testregel)

    mockMvc
      .perform(get("/v1/testreglar/42"))
      .andExpect(status().isOk)
      .andExpect(jsonPath("$.id").value(42))
      .andExpect(jsonPath("$.testregelId").value("MANUELL-FORENKLA-42"))
      .andExpect(jsonPath("$.modus").value("manuellForenkla"))
      .andExpect(jsonPath("$.definition.description").value("Forenkla definisjon"))
      .andExpect(jsonPath("$.definition.utfall", hasSize<Any>(1)))
      .andExpect(jsonPath("$.definition.utfall[0].beskrivelse").value("Alt ok"))
      .andExpect(jsonPath("$.definition.utfall[0].testresultat").value("samsvar"))
      .andExpect(jsonPath("$.definition.key").doesNotExist())
  }
}
