package no.uutilsynet.testlab2krav.testregel

import java.time.Instant
import no.uutilsynet.testlab2.constants.ManuellForenklaTestregelDefinition
import no.uutilsynet.testlab2.constants.StringTestregelDefinition
import no.uutilsynet.testlab2.constants.TestlabLocale
import no.uutilsynet.testlab2.constants.TestregelInnholdstype
import no.uutilsynet.testlab2.constants.TestregelModus
import no.uutilsynet.testlab2.constants.TestregelStatus
import no.uutilsynet.testlab2.constants.TestregelUtfall
import no.uutilsynet.testlab2.constants.TestresultatUtfall
import no.uutilsynet.testlab2krav.dao.KravDAO
import no.uutilsynet.testlab2krav.testregel.TestConstants.name
import no.uutilsynet.testlab2krav.testregel.TestConstants.testregelSchemaAutomatisk
import no.uutilsynet.testlab2krav.testregel.TestConstants.testregelTestKravId
import no.uutilsynet.testlab2krav.testregel.model.TestregelInit
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(properties = ["spring.datasource.url= jdbc:tc:postgresql:16-alpine:///test-db"])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class TestregelDAOTest(@Autowired val testregelDAO: TestregelDAO, @Autowired val kravDAO: KravDAO) {

  val deleteThese: MutableList<Int> = mutableListOf()

  @AfterAll
  fun cleanup() {
    deleteThese.forEach { testregelDAO.deleteTestregel(it) }
  }

  @Test
  @DisplayName("Skal hente testregel fra DAO")
  fun getTestregel() {
    val id = createTestregel()
    val testregel = testregelDAO.getTestregel(id)
    Assertions.assertThat(testregel).isNotNull
    Assertions.assertThat(testregel?.namn).isEqualTo(name)
  }

  @Test
  @DisplayName("Skal hente testregelliste fra DAO")
  fun getTestregelList() {
    val id = createTestregel()
    val testregel = testregelDAO.getTestregel(id)
    Assertions.assertThat(testregel!!.id).isEqualTo(id)
    val list = testregelDAO.getTestregelList()

    Assertions.assertThat(list).contains(testregel)
  }

  @Test
  @DisplayName("Skal opprette testregel i DAO")
  fun insertTestregel() {
    val id = assertDoesNotThrow { createTestregel() }
    val testregel = testregelDAO.getTestregel(id)
    Assertions.assertThat(testregel).isNotNull
  }

  @Test
  @DisplayName("Skal slette testregel i DAO")
  fun deleteTestregel() {
    val id = createTestregel()
    val existingTestregel = testregelDAO.getTestregel(id)
    Assertions.assertThat(existingTestregel).isNotNull

    testregelDAO.deleteTestregel(id)

    val nonExistingTestregel = testregelDAO.getTestregel(id)
    Assertions.assertThat(nonExistingTestregel).isNull()
  }

  @Test
  @DisplayName("Skal oppdatere testregel i DAO")
  fun updateTestregel() {
    val testregelInit =
      TestregelInit(
        testregelId = "QW-ACT-R1",
        namn = "test_skal_slettes_1",
        kravId = 1,
        status = TestregelStatus.publisert,
        type = TestregelInnholdstype.nett,
        modus = TestregelModus.automatisk,
        spraak = TestlabLocale.nb,
        testregelSchema = "",
        innhaldstypeTesting = 1,
        tema = 1,
        testobjekt = 1,
        kravTilSamsvar = "",
        definition = StringTestregelDefinition(""))
    val id = createTestregel(testregelInit)

    val oldTestregel = testregelDAO.getTestregel(id)
    Assertions.assertThat(oldTestregel).isNotNull
    Assertions.assertThat(oldTestregel?.kravId).isEqualTo(testregelInit.kravId)
    Assertions.assertThat(oldTestregel?.testregelSchema).isEqualTo(testregelInit.testregelSchema)
    Assertions.assertThat(oldTestregel?.namn).isEqualTo(testregelInit.namn)

    oldTestregel
      ?.copy(kravId = testregelTestKravId, testregelSchema = testregelSchemaAutomatisk, namn = name)
      ?.let { testregelDAO.updateTestregel(it) }

    val updatedTestregel = testregelDAO.getTestregel(id)
    Assertions.assertThat(updatedTestregel).isNotNull
    Assertions.assertThat(updatedTestregel?.kravId).isEqualTo(testregelTestKravId)
    Assertions.assertThat(updatedTestregel?.testregelSchema).isEqualTo(testregelSchemaAutomatisk)
    Assertions.assertThat(updatedTestregel?.namn).isEqualTo(name)
  }

  @Test
  fun updateTestregelSetDatoSistEndra() {
    val testregelInit =
      TestregelInit(
        testregelId = "QW-ACT-R1",
        namn = "test_skal_slettes_1",
        kravId = 1,
        status = TestregelStatus.publisert,
        type = TestregelInnholdstype.nett,
        modus = TestregelModus.automatisk,
        spraak = TestlabLocale.nb,
        datoSistEndra = Instant.now().minusSeconds(61),
        testregelSchema = "",
        innhaldstypeTesting = 1,
        tema = 1,
        testobjekt = 1,
        kravTilSamsvar = "",
        definition = StringTestregelDefinition(""))
    val id = createTestregel(testregelInit)

    val oldTestregel = testregelDAO.getTestregel(id)
    Assertions.assertThat(oldTestregel).isNotNull
    Assertions.assertThat(oldTestregel?.kravId).isEqualTo(testregelInit.kravId)
    Assertions.assertThat(oldTestregel?.testregelSchema).isEqualTo(testregelInit.testregelSchema)
    Assertions.assertThat(oldTestregel?.namn).isEqualTo(testregelInit.namn)

    val oldDate = oldTestregel?.datoSistEndra

    oldTestregel
      ?.copy(kravId = testregelTestKravId, testregelSchema = testregelSchemaAutomatisk, namn = name)
      ?.let { testregelDAO.updateTestregel(it) }

    val updatedTestregel = testregelDAO.getTestregel(id)
    Assertions.assertThat(updatedTestregel).isNotNull
    Assertions.assertThat(updatedTestregel?.kravId).isEqualTo(testregelTestKravId)
    Assertions.assertThat(updatedTestregel?.testregelSchema).isEqualTo(testregelSchemaAutomatisk)
    Assertions.assertThat(updatedTestregel?.namn).isEqualTo(name)

    val newDate = updatedTestregel?.datoSistEndra

    Assertions.assertThat(newDate).isAfter(oldDate)
  }

  @Test
  @DisplayName("Skal handtere testregel_utfall for manuell-forenkla testregel")
  fun crudTestregelUtfallForManuellForenkla() {
    val schemaWithUtfall =
      """
      {
        "utfall": [
          {"id": 1, "beskrivelse": "Fyrste utfall", "testresultat": "samsvar", "default": true},
          {"id": 2, "beskrivelse": "Andre utfall", "testresultat": "brot", "default": false}
        ]
      }
      """
        .trimIndent()

    val id =
      createTestregel(
        TestregelInit(
          testregelId = "MANUELL-FORENKLA-1",
          namn = "manuell_forenkla_testregel",
          kravId = testregelTestKravId,
          status = TestregelStatus.publisert,
          type = TestregelInnholdstype.nett,
          modus = TestregelModus.manuellForenkla,
          spraak = TestlabLocale.nb,
          testregelSchema = schemaWithUtfall,
          innhaldstypeTesting = 1,
          tema = 1,
          testobjekt = 1,
          kravTilSamsvar = "",
          definition = StringTestregelDefinition("")))

    val created = testregelDAO.getTestregel(id)
    Assertions.assertThat(created).isNotNull
    Assertions.assertThat(created?.modus).isEqualTo(TestregelModus.manuellForenkla)

    val createdDefinition = created?.definition as ManuellForenklaTestregelDefinition
    Assertions.assertThat(createdDefinition.utfall).hasSize(2)
    Assertions.assertThat(createdDefinition.utfall.map { it.beskrivelse })
      .containsExactly("Fyrste utfall", "Andre utfall")

    val updated =
      created.copy(
        testregelSchema = "{\"utfall\": []}",
        definition =
          ManuellForenklaTestregelDefinition(
            description = "oppdatert",
            helptext = "oppdatert",
            utfall =
              listOf(
                TestregelUtfall(
                  id = 10,
                  beskrivelse = "Oppdatert utfall",
                  testresultat = TestresultatUtfall.varsel,
                  default = true))),
      )

    testregelDAO.updateTestregel(updated)

    val fetchedAfterUpdate = testregelDAO.getTestregel(id)
    val updatedDefinition = fetchedAfterUpdate?.definition as ManuellForenklaTestregelDefinition
    Assertions.assertThat(updatedDefinition.utfall).hasSize(1)
    Assertions.assertThat(updatedDefinition.utfall.first().beskrivelse)
      .isEqualTo("Oppdatert utfall")
    Assertions.assertThat(updatedDefinition.utfall.first().testresultat)
      .isEqualTo(TestresultatUtfall.varsel)

    assertDoesNotThrow { testregelDAO.deleteTestregel(id) }
    Assertions.assertThat(testregelDAO.getTestregel(id)).isNull()
  }

  private fun createTestregel(
    testregelInit: TestregelInit =
      TestregelInit(
        testregelId = "QW-ACT-R1",
        namn = name,
        kravId = testregelTestKravId,
        status = TestregelStatus.publisert,
        type = TestregelInnholdstype.nett,
        modus = TestregelModus.automatisk,
        spraak = TestlabLocale.nb,
        testregelSchema = testregelSchemaAutomatisk,
        innhaldstypeTesting = 1,
        tema = 1,
        testobjekt = 1,
        kravTilSamsvar = "",
        definition = StringTestregelDefinition(""))
  ): Int {

    val id = testregelDAO.createTestregel(testregelInit).also { deleteThese.add(it) }
    return id
  }
}
