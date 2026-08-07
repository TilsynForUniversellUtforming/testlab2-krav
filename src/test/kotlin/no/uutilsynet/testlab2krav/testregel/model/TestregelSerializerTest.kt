package no.uutilsynet.testlab2krav.testregel.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.time.Instant
import no.uutilsynet.testlab2.constants.ManuellForenklaTestregelDefinition
import no.uutilsynet.testlab2.constants.StringTestregelDefinition
import no.uutilsynet.testlab2.constants.TestlabLocale
import no.uutilsynet.testlab2.constants.TestregelInnholdstype
import no.uutilsynet.testlab2.constants.TestregelModus
import no.uutilsynet.testlab2.constants.TestregelStatus
import no.uutilsynet.testlab2.constants.TestregelUtfall
import no.uutilsynet.testlab2.constants.TestresultatUtfall
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TestregelSerializerTest {

  private val objectMapper = jacksonObjectMapper()

  @Test
  fun `serialize automatisk testregel with qualweb definition`() {
    val testregel =
      Testregel(
        id = 1,
        testregelId = "QW-ACT-R1",
        versjon = 1,
        namn = "Automatisk testregel",
        kravId = 10,
        status = TestregelStatus.publisert,
        datoSistEndra = Instant.parse("2026-01-01T10:00:00Z"),
        type = TestregelInnholdstype.nett,
        modus = TestregelModus.automatisk,
        spraak = TestlabLocale.nb,
        tema = 1,
        testobjekt = 2,
        kravTilSamsvar = "krav",
        testregelSchema = "QW-ACT-R1",
        innhaldstypeTesting = 3,
        definition = StringTestregelDefinition("ignored"))

    val json = objectMapper.writeValueAsString(testregel)
    val tree = objectMapper.readTree(json)

    assertThat(tree.get("definition").get("type").asText()).isEqualTo("qualweb")
    assertThat(tree.get("definition").get("key").asText()).isEqualTo("QW-ACT-R1")
    assertThat(tree.get("definition").get("body")).isNull()
  }

  @Test
  fun `serialize manuell forenkla testregel with utfall definition`() {
    val testregel =
      Testregel(
        id = 2,
        testregelId = "MANUELL-1",
        versjon = 1,
        namn = "Manuell forenkla testregel",
        kravId = 11,
        status = TestregelStatus.publisert,
        datoSistEndra = Instant.parse("2026-01-01T11:00:00Z"),
        type = TestregelInnholdstype.nett,
        modus = TestregelModus.manuellForenkla,
        spraak = TestlabLocale.nb,
        tema = 1,
        testobjekt = 2,
        kravTilSamsvar = "krav",
        testregelSchema = "{\"utfall\":[]}",
        innhaldstypeTesting = 3,
        definition =
          ManuellForenklaTestregelDefinition(
            description = "forenkla",
            utfall =
              listOf(
                TestregelUtfall(
                  id = 1,
                  beskrivelse = "Utfall A",
                  testresultat = TestresultatUtfall.samsvar,
                  default = true))))

    val json = objectMapper.writeValueAsString(testregel)
    val tree = objectMapper.readTree(json)

    assertThat(tree.get("definition").get("type").asText()).isEqualTo("manuell-forenkla")
    assertThat(tree.get("definition").get("description").asText()).isEqualTo("forenkla")
    assertThat(tree.get("definition").get("utfall").size()).isEqualTo(1)
    assertThat(tree.get("definition").get("utfall").first().get("beskrivelse").asText())
      .isEqualTo("Utfall A")
  }
}
