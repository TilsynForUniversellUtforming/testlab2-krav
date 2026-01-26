package no.uutilsynet.testlab2krav.testregel.model

import java.time.Instant
import no.uutilsynet.testlab2.constants.TestlabLocale
import no.uutilsynet.testlab2.constants.TestregelInnholdstype
import no.uutilsynet.testlab2.constants.TestregelModus
import no.uutilsynet.testlab2.constants.TestregelStatus
import no.uutilsynet.testlab2.validators.validateNamn
import no.uutilsynet.testlab2.validators.validateTestregelId
import no.uutilsynet.testlab2krav.dto.KravWcag2x
import no.uutilsynet.testlab2krav.testregel.validateSchema

data class Testregel(
  val id: Int,
  val testregelId: String,
  val versjon: Int,
  val namn: String,
  val kravId: Int,
  val status: TestregelStatus,
  val datoSistEndra: Instant = Instant.now(),
  val type: TestregelInnholdstype,
  val modus: TestregelModus,
  val spraak: TestlabLocale,
  val tema: Int?,
  val testobjekt: Int?,
  val kravTilSamsvar: String?,
  val testregelSchema: String,
  val innhaldstypeTesting: Int?,
) {
  companion object {
    fun Testregel.validateTestregel(): Result<Testregel> = runCatching {
      val name = validateNamn(this.namn).getOrThrow()
      val testregelId = validateTestregelId(this.testregelId).getOrThrow()
      val schema = validateSchema(this.testregelSchema, this.modus).getOrThrow()

      Testregel(
        this.id,
        testregelId,
        this.versjon,
        name,
        kravId,
        this.status,
        this.datoSistEndra,
        this.type,
        modus,
        this.spraak,
        this.tema,
        this.testobjekt,
        this.kravTilSamsvar,
        schema,
        this.innhaldstypeTesting)
    }
  }
}

data class TestregelKrav(
  val id: Int,
  val testregelId: String,
  val versjon: Int,
  val namn: String,
  val krav: KravWcag2x,
  val status: TestregelStatus,
  val datoSistEndra: Instant = Instant.now(),
  val type: TestregelInnholdstype,
  val modus: TestregelModus,
  val spraak: TestlabLocale,
  val tema: Int?,
  val testobjekt: Int?,
  val kravTilSamsvar: String?,
  val testregelSchema: String,
  val innhaldstypeTesting: Int?,
) {

  constructor(
    testregel: Testregel,
    krav: KravWcag2x
  ) : this(
    testregel.id,
    testregel.testregelId,
    testregel.versjon,
    testregel.namn,
    krav,
    testregel.status,
    testregel.datoSistEndra,
    testregel.type,
    testregel.modus,
    testregel.spraak,
    testregel.tema,
    testregel.testobjekt,
    testregel.kravTilSamsvar,
    testregel.testregelSchema,
    testregel.innhaldstypeTesting)
}
