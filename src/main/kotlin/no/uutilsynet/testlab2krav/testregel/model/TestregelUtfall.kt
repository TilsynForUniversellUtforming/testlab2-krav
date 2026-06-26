package no.uutilsynet.testlab2krav.testregel.model

import no.uutilsynet.testlab2.constants.TestresultatUtfall

data class TestregelUtfall(
  val id: Int,
  val beskrivelse: String,
  val testresultat: TestresultatUtfall,
  val default: Boolean = false
)
