package no.uutilsynet.testlab2krav.dao

import no.uutilsynet.testlab2krav.dto.Krav
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class KravDao(val jdbcTemplate: JdbcTemplate) {

  private final val listKravSql: String = "select * from krav"

  fun listKrav(): List<Krav> =
    jdbcTemplate.query(listKravSql, DataClassRowMapper.newInstance(Krav::class.java))
}
