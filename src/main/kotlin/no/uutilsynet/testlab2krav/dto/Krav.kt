package no.uutilsynet.testlab2krav.dto

import java.net.URL
import no.uutilsynet.testlab2.constants.KravStatus

interface Krav {
  val tittel: String
  val status: KravStatus
  val innhald: String?
  val gjeldAutomat: Boolean
  val gjeldNettsider: Boolean
  val gjeldApp: Boolean
  val urlRettleiing: URL?
  val kommentarBrudd: String?
}
