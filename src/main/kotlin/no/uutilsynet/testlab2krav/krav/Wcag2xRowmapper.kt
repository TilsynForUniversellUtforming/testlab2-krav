package no.uutilsynet.testlab2krav.krav

import java.net.URI
import java.net.URL
import java.sql.ResultSet
import no.uutilsynet.testlab2.constants.KravStatus
import no.uutilsynet.testlab2.constants.WcagPrinsipp
import no.uutilsynet.testlab2.constants.WcagRetninglinje
import no.uutilsynet.testlab2.constants.WcagSamsvarsnivaa
import no.uutilsynet.testlab2krav.dto.KravWcag2x
import no.uutilsynet.testlab2krav.findBy
import org.springframework.jdbc.core.RowMapper

class Wcag2xRowmapper : RowMapper<KravWcag2x> {
  override fun mapRow(rs: ResultSet, rowNum: Int): KravWcag2x {
    return KravWcag2x(
      rs.getInt("id"),
      rs.getString("tittel"),
      KravStatus::status.findBy(rs.getString("status")) ?: KravStatus.gjeldande,
      rs.getString("innhald"),
      rs.getBoolean("gjeldautomat"),
      rs.getBoolean("gjeldnettsider"),
      rs.getBoolean("gjeldapp"),
      validUrlString(rs.getString("urlrettleiing")),
      WcagPrinsipp::prinsipp.findBy(rs.getString("prinsipp")),
      WcagRetninglinje::retninglinje.findBy(rs.getString("retningslinje")),
      rs.getString("suksesskriterium"),
      WcagSamsvarsnivaa::nivaa.findBy(rs.getString("samsvarsnivaa")),
      rs.getString("kommentar_brudd"))
  }

  private fun validUrlString(urlString: String?): URL? {
    if (urlString.isNullOrEmpty()) return null
    return URI(urlString).toURL()
  }
}
