package no.uutilsynet.testlab2krav.dao

import no.uutilsynet.testlab2krav.dao.KravDAO.KravParams.kravRowmapper
import no.uutilsynet.testlab2krav.dao.KravDAO.KravParams.listKravSql
import no.uutilsynet.testlab2krav.dao.KravDAO.KravParams.listWcag2xSql
import no.uutilsynet.testlab2krav.dao.KravDAO.KravParams.selectById
import no.uutilsynet.testlab2krav.dao.KravDAO.KravParams.selectBySuksesskriterium
import no.uutilsynet.testlab2krav.dao.KravDAO.KravParams.wcag2xKravRowmapper
import no.uutilsynet.testlab2krav.dto.Krav
import no.uutilsynet.testlab2krav.dto.KravWcag2x
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class KravDAO(val jdbcTemplate: NamedParameterJdbcTemplate) {

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
                    samsvarsnivaa
                from testlab2_krav.wcag2krav
            """
                .trimIndent()

        val selectById = "$listWcag2xSql where id = :id"

        val selectBySuksesskriterium = "$listWcag2xSql where suksesskriterium = :suksesskriterium"

        val kravRowmapper = DataClassRowMapper.newInstance(Krav::class.java)
        val wcag2xKravRowmapper = DataClassRowMapper.newInstance(KravWcag2x::class.java)
    }

    fun listKrav(): List<Krav> = jdbcTemplate.query(listKravSql, kravRowmapper)

    fun listWcagKrav(): List<KravWcag2x> = jdbcTemplate.query(listWcag2xSql, wcag2xKravRowmapper)

    fun getWcagKrav(id: Int): KravWcag2x =
        jdbcTemplate.query(selectById, mapOf("id" to id), wcag2xKravRowmapper).firstOrNull()
            ?: throw IllegalArgumentException("Krav med id $id finnes ikkje")

    fun getKravBySuksesskriterium(suksesskriterium: String): KravWcag2x =
        jdbcTemplate
            .query(
                selectBySuksesskriterium,
                mapOf("suksesskriterium" to suksesskriterium),
                wcag2xKravRowmapper)
            .firstOrNull()
            ?: throw IllegalArgumentException(
                "Krav med suksesskriterium $suksesskriterium finnes ikkje")

    @Transactional
    fun createWcagKrav(krav: KravWcag2x): Int {

        val kravId =
            jdbcTemplate.update(
                """
            insert into testlab2_krav.wcag2krav ( tittel, status, innhald, gjeldautomat, gjeldnettsider, gjeldapp, urlrettleiing,prinsipp, retningslinje, suksesskriterium, samsvarsnivaa)
                values (:tittel, :status, :innhald, :gjeldautomat, :gjeldnettsider, :gjeldapp, :urlrettleiing, :prinsipp, :retningslinje, :suksesskriterium, :samsvarsnivaa)
            """,
                mapOf(
                    "tittel" to krav.tittel,
                    "status" to krav.status,
                    "innhald" to krav.innhald,
                    "gjeldautomat" to krav.gjeldAutomat,
                    "gjeldnettsider" to krav.gjeldNettsider,
                    "gjeldapp" to krav.gjeldApp,
                    "urlrettleiing" to krav.urlRettleiing,
                    "prinsipp" to krav.prinsipp,
                    "retningslinje" to krav.retningslinje,
                    "suksesskriterium" to krav.suksesskriterium,
                    "samsvarsnivaa" to krav.samsvarsnivaa))
        return kravId
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
                    samsvarsnivaa = :samsvarsnivaa
                where id = :id
            """

        var rows =
            jdbcTemplate.update(
                updateKravSql,
                mapOf(
                    "id" to krav.id,
                    "tittel" to krav.tittel,
                    "status" to krav.status,
                    "innhald" to krav.innhald,
                    "gjeldautomat" to krav.gjeldAutomat,
                    "gjeldnettsider" to krav.gjeldNettsider,
                    "gjeldapp" to krav.gjeldApp,
                    "urlrettleiing" to krav.urlRettleiing,
                    "prinsipp" to krav.prinsipp,
                    "retningslinje" to krav.retningslinje,
                    "suksesskriterium" to krav.suksesskriterium,
                    "samsvarsnivaa" to krav.samsvarsnivaa))

        return rows
    }
}
