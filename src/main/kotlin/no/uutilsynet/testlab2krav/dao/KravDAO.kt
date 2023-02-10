package no.uutilsynet.testlab2krav.dao

import no.uutilsynet.testlab2krav.dao.KravDAO.KravParams.kravRowmapper
import no.uutilsynet.testlab2krav.dao.KravDAO.KravParams.listKravSql
import no.uutilsynet.testlab2krav.dto.Krav
import no.uutilsynet.testlab2krav.krav.KravApi
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component

@Component
class KravDAO(val jdbcTemplate: NamedParameterJdbcTemplate) : KravApi {

    object KravParams {

        const val listKravSql: String = "select * from krav"

        val kravRowmapper = DataClassRowMapper.newInstance(Krav::class.java)
    }

    override fun listKrav(): List<Krav> = jdbcTemplate.query(listKravSql, kravRowmapper)
}
