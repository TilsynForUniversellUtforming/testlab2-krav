package no.uutilsynet.testlab2krav.testregel

import java.net.URI
import no.uutilsynet.testlab2.constants.KravStatus
import no.uutilsynet.testlab2.constants.WcagPrinsipp
import no.uutilsynet.testlab2.constants.WcagRetninglinje
import no.uutilsynet.testlab2.constants.WcagSamsvarsnivaa
import no.uutilsynet.testlab2krav.dao.KravDAO
import no.uutilsynet.testlab2krav.dto.KravWcag2x
import no.uutilsynet.testlab2krav.testregel.TestConstants.modus
import no.uutilsynet.testlab2krav.testregel.TestConstants.name
import no.uutilsynet.testlab2krav.testregel.TestConstants.testregelCreateRequestBody
import no.uutilsynet.testlab2krav.testregel.TestConstants.testregelSchemaAutomatisk
import no.uutilsynet.testlab2krav.testregel.TestConstants.testregelTestKravId
import no.uutilsynet.testlab2krav.testregel.model.Testregel
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.test.web.servlet.client.expectBody
import org.springframework.test.web.servlet.client.returnResult
import org.springframework.web.context.WebApplicationContext

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["spring.datasource.url= jdbc:tc:postgresql:16-alpine:///test-db"],
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class TestregelIntegrationTests {

  @MockitoBean private lateinit var kravregisterClient: KravDAO

  lateinit var client: RestTestClient

  @BeforeEach
  fun setUp(context: WebApplicationContext) { // Inject the configuration
    client = RestTestClient.bindToApplicationContext(context).build()
  }

  @BeforeAll
  fun beforeAll() {
    Mockito.`when`(kravregisterClient.getWcagKrav(1))
        .thenReturn(
            KravWcag2x(
                1,
                "1.1.1 Ikke-tekstlig innhold,Gjeldande",
                KravStatus.gjeldande,
                "Innhald",
                false,
                false,
                false,
                URI("https://www.uutilsynet.no/wcag-standarden/111-ikke-tekstlig-innhold-niva/87")
                    .toURL(),
                WcagPrinsipp.robust,
                WcagRetninglinje.leselig,
                "1.1.1",
                WcagSamsvarsnivaa.A,
                "kommentar",
            )
        )
  }

  val deleteThese: MutableList<Int> = mutableListOf()

  @Test
  @DisplayName("Skal kunne opprette en testregel")
  fun createTestregel() {
    val locationPattern = """/v1/testreglar/\d+"""
    val location =
        client
            .post()
            .uri("/v1/testreglar")
            .body(testregelCreateRequestBody)
            .exchange()
            .expectStatus()
            .isCreated
            .expectHeader()
            .exists("Location")
            .returnResult<Void>()
            .responseHeaders
            .location

    Assertions.assertThat(location).isNotNull()
    deleteThese.add(idFromLocation(location!!))

    Assertions.assertThat(location.toString()).matches(locationPattern)
  }

  @Test
  @DisplayName("Skal ikke kunne opprette en testregel med feil request")
  fun createTestregelErrors() {
    client
        .post()
        .uri("/v1/testreglar")
        .body(
            mapOf(
                "kravId" to "1",
                "testregelSchema" to testregelSchemaAutomatisk,
                "name" to name,
                "type" to "automatisk",
            )
        )
        .exchange()
        .expectStatus()
        .isBadRequest
  }

  @Test
  @DisplayName("Skal ikke kunne opprette en testregel hvis krav ikke finnes")
  fun createTestregelKravError() {
    Mockito.`when`(kravregisterClient.getWcagKrav(1)).thenThrow(RuntimeException())

    client
        .post()
        .uri("/v1/testreglar")
        .body(testregelCreateRequestBody)
        .exchange()
        .expectStatus()
        .isBadRequest
  }

  @Test
  @DisplayName("Skal kunne slette testregel")
  fun deleteTestregel() {
    val location = createDefaultTestregel()
    val testregel =
        client
            .get()
            .uri(location)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<Testregel>()
            .returnResult()
            .responseBody

    Assertions.assertThat(testregel).isNotNull

    client.delete().uri(location).exchange().expectStatus().isOk

    client.get().uri(location).exchange().expectStatus().isNotFound
  }

  @Test
  @DisplayName("Skal kunne oppdatere testregel")
  fun updateTestregel() {
    val location = createDefaultTestregel()
    val testregel =
        client
            .get()
            .uri(location)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<Testregel>()
            .returnResult()
            .responseBody

    val kravId = 2
    Assertions.assertThat(testregel?.kravId).isNotEqualTo(kravId)

    val updatedTestregel = testregel?.copy(kravId = kravId)
    if (updatedTestregel != null) {
      client.put().uri("/v1/testreglar").body(updatedTestregel).exchange().expectStatus().isOk
    }

    val fetchedTestregel =
        client
            .get()
            .uri(location)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<Testregel>()
            .returnResult()
            .responseBody

    Assertions.assertThat(fetchedTestregel?.kravId).isEqualTo(kravId)
  }

  @Nested
  @DisplayName("Hvis det finnes en testregel i databasen")
  inner class DatabaseHasAtLeastOneTestregel {
    private val location = createDefaultTestregel()

    @Test
    @DisplayName("Skal hente testregel")
    fun getTestregel() {
      val testregel =
          client
              .get()
              .uri(location)
              .exchange()
              .expectStatus()
              .isOk
              .expectBody<Testregel>()
              .returnResult()
              .responseBody

      Assertions.assertThat(testregel?.kravId).isEqualTo(testregelTestKravId)
      Assertions.assertThat(testregel?.testregelSchema).isEqualTo(testregelSchemaAutomatisk)
      Assertions.assertThat(testregel?.namn).isEqualTo(name)
    }

    @Test
    @DisplayName("Skal hente liste med testregel")
    fun getTestregelList() {
      val testregel =
          client
              .get()
              .uri(location)
              .exchange()
              .expectStatus()
              .isOk
              .expectBody<Testregel>()
              .returnResult()
              .responseBody

      val testregelList =
          client
              .get()
              .uri("/v1/testreglar")
              .exchange()
              .expectStatus()
              .isOk
              .expectBody<List<Testregel>>()
              .returnResult()
              .responseBody

      val testregelFromList = testregelList?.find { it.id == testregel?.id }

      Assertions.assertThat(testregelFromList?.kravId).isEqualTo(testregelTestKravId)
      Assertions.assertThat(testregelFromList?.modus).isEqualTo(modus)
      Assertions.assertThat(testregelFromList?.namn).isEqualTo(name)
    }
  }

  private fun createDefaultTestregel(): URI =
      client
          .post()
          .uri("/v1/testreglar")
          .body(testregelCreateRequestBody)
          .exchange()
          .expectStatus()
          .isCreated
          .expectHeader()
          .exists("Location")
          .returnResult<Void>()
          .responseHeaders
          .location
          ?.also { deleteThese.add(idFromLocation(it)) } ?: error("No location header in response")

  private fun idFromLocation(location: URI) = location.path.split("/").last().toInt()
}
