package no.uutilsynet.testlab2krav.dao

import no.uutilsynet.testlab2krav.dto.Krav
import no.uutilsynet.testlab2krav.util.KravJdbcUtil.FIND_ALL
import no.uutilsynet.testlab2krav.util.KravJdbcUtil.FIND_BY_ID
import no.uutilsynet.testlab2krav.util.KravJdbcUtil.ID_PARAM
import no.uutilsynet.testlab2krav.util.KravJdbcUtil.kravRowmapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service

@Service
class KravDao(val jdbcTemplate: NamedParameterJdbcTemplate) {

    fun listKrav(): List<Krav> = jdbcTemplate.query(FIND_ALL, kravRowmapper)

    fun findById(id: Int): Krav? {
        val params = MapSqlParameterSource(ID_PARAM, id)
        return jdbcTemplate.queryForObject(FIND_BY_ID, params, kravRowmapper)
    }
}
