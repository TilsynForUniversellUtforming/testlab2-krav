package no.uutilsynet.testlab2krav.dto

import java.time.LocalDateTime

data class KravTolking(
  val id: Int,
  val kravId: Int,
  val versjon: String,
  val formaal: String,
  val tolkingOgPresiseringAvKrav: String,
  val sistOppdatert: LocalDateTime,
  val innhald: String, // TODO HTML-type?
  val status: String,
  val dekkaAvACTReglar: Boolean,
  val lenkerACTReglar: String // TODO - JSONObject?
)
