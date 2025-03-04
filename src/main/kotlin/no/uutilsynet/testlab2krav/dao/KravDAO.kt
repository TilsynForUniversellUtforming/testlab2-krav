package no.uutilsynet.testlab2krav.dao

import no.uutilsynet.testlab2krav.dao.KravDAO.KravParams.kravRowmapper
import no.uutilsynet.testlab2krav.dao.KravDAO.KravParams.listKravSql
import no.uutilsynet.testlab2krav.dao.KravDAO.KravParams.listWcag2xSql
import no.uutilsynet.testlab2krav.dao.KravDAO.KravParams.selectById
import no.uutilsynet.testlab2krav.dao.KravDAO.KravParams.selectBySuksesskriterium
import no.uutilsynet.testlab2krav.dto.Krav
import no.uutilsynet.testlab2krav.dto.KravInit
import no.uutilsynet.testlab2krav.dto.KravWcag2x
import no.uutilsynet.testlab2krav.krav.Wcag2xRowmapper
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class KravDAO(val jdbcTemplate: NamedParameterJdbcTemplate) {

  val logger = LoggerFactory.getLogger(KravDAO::class.java)

  object KravParams {

    val listKravSql: String =
      """
            select id,
                    tittel,
                    status,
                    innhald,
                    gjeldautomat,
                    gjeldnettsider,
                    gjeldapp,
                    urlrettleiing 
                from testlab2_krav.krav
            """
        .trimIndent()

    val listWcag2xSql: String =
      """
            select id,
                    tittel,
                    status,
                    innhald,
                    gjeldautomat,
                    gjeldnettsider,
                    gjeldapp,
                    urlrettleiing,
                    prinsipp,
                    retningslinje,
                    suksesskriterium,
                    samsvarsnivaa,
                    kommentar_brudd
                from testlab2_krav.wcag2krav
            """
        .trimIndent()

    val selectById = "$listWcag2xSql where id = :id"

    val selectBySuksesskriterium = "$listWcag2xSql where suksesskriterium = :suksesskriterium"

    val kravRowmapper = DataClassRowMapper.newInstance(Krav::class.java)
  }

  fun listKrav(): List<Krav> = jdbcTemplate.query(listKravSql, kravRowmapper)

  fun listWcagKrav(): List<KravWcag2x> {
    return jdbcTemplate.query(listWcag2xSql, Wcag2xRowmapper())
  }

  fun getWcagKrav(id: Int): KravWcag2x =
    jdbcTemplate.query(selectById, mapOf("id" to id), Wcag2xRowmapper()).firstOrNull()
      ?: throw IllegalArgumentException("Krav med id $id finnes ikkje")

  fun getKravBySuksesskriterium(suksesskriterium: String): KravWcag2x =
    jdbcTemplate
      .query(
        selectBySuksesskriterium, mapOf("suksesskriterium" to suksesskriterium), Wcag2xRowmapper())
      .firstOrNull()
      ?: throw IllegalArgumentException("Krav med suksesskriterium $suksesskriterium finnes ikkje")

  @Transactional
  fun createWcagKrav(krav: KravInit): Int {
    logger.info("Create Krav: $krav")

    val keyHolder: KeyHolder = GeneratedKeyHolder()

    val params = MapSqlParameterSource()
    params.addValue("tittel", krav.tittel)
    params.addValue("status", krav.status.name)
    params.addValue("innhald", krav.innhald)
    params.addValue("gjeldautomat", krav.gjeldAutomat)
    params.addValue("gjeldnettsider", krav.gjeldNettsider)
    params.addValue("gjeldapp", krav.gjeldApp)
    params.addValue("urlrettleiing", krav.urlRettleiing.toString())
    params.addValue("prinsipp", krav.prinsipp?.prinsipp)
    params.addValue("retningslinje", krav.retningslinje?.retninglinje)
    params.addValue("suksesskriterium", krav.suksesskriterium)
    params.addValue("samsvarsnivaa", krav.samsvarsnivaa?.nivaa)
    params.addValue("kommentarBrudd", krav.kommentarBrudd)

    jdbcTemplate.update(
      """
          insert into testlab2_krav.wcag2krav ( tittel, status, innhald, gjeldautomat, gjeldnettsider, gjeldapp, urlrettleiing,prinsipp, retningslinje, suksesskriterium, samsvarsnivaa,kommentar_brudd)
              values (:tittel, :status, :innhald, :gjeldautomat, :gjeldnettsider, :gjeldapp, :urlrettleiing, :prinsipp, :retningslinje, :suksesskriterium, :samsvarsnivaa,:kommentarBrudd)
          """,
      params,
      keyHolder)

    return keyHolder.keys?.get("id") as Int
  }

  @Transactional
  fun deleteKrav(kravid: Int): Boolean {
    val rows =
      jdbcTemplate.update(
        """
            delete from testlab2_krav.wcag2krav where id = :kravid
            """,
        mapOf("kravid" to kravid))
    return rows > 0
  }

  @Transactional
  fun updateWcagKrav(krav: KravWcag2x): Int {
    val updateKravSql =
      """
            update testlab2_krav.wcag2krav
                set tittel = :tittel,
                    status = :status,
                    innhald = :innhald,
                    gjeldautomat = :gjeldautomat,
                    gjeldnettsider = :gjeldnettsider,
                    gjeldapp = :gjeldapp,
                    urlrettleiing = :urlrettleiing,
                    prinsipp = :prinsipp,
                    retningslinje = :retningslinje,
                    suksesskriterium = :suksesskriterium,
                    samsvarsnivaa = :samsvarsnivaa,
                    kommentar_brudd = :kommentarBrudd
                where id = :id
            """

    val rows =
      jdbcTemplate.update(
        updateKravSql,
        mapOf(
          "id" to krav.id,
          "tittel" to krav.tittel,
          "status" to krav.status.status,
          "innhald" to krav.innhald,
          "gjeldautomat" to krav.gjeldAutomat,
          "gjeldnettsider" to krav.gjeldNettsider,
          "gjeldapp" to krav.gjeldApp,
          "urlrettleiing" to krav.urlRettleiing.toString(),
          "prinsipp" to krav.prinsipp?.prinsipp,
          "retningslinje" to krav.retningslinje?.retninglinje,
          "suksesskriterium" to krav.suksesskriterium,
          "samsvarsnivaa" to krav.samsvarsnivaa?.nivaa,
          "kommentarBrudd" to krav.kommentarBrudd))

    return rows
  }
}
