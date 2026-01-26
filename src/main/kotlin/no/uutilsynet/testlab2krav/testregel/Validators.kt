package no.uutilsynet.testlab2krav.testregel

import no.uutilsynet.testlab2.constants.TestregelModus
import no.uutilsynet.testlab2.validators.validateJSONString

fun validateSchema(testregelSchema: String?, modus: TestregelModus): Result<String> = runCatching {
  require(!testregelSchema.isNullOrBlank()) { "Testregel-skjema kan ikkje vera blank" }

  require(
      !(modus == TestregelModus.automatisk &&
          !testregelSchema.matches("^(QW-ACT-R)[0-9]{1,2}$".toRegex()))) {
        "QualWeb regel id må vera på formen QW-ACT-RXX"
      }
  require(!(modus == TestregelModus.manuell && validateJSONString(testregelSchema).isFailure)) {
    "Skjema må væra på gylig json-format"
  }

  testregelSchema
}
