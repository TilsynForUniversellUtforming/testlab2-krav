package no.uutilsynet.testlab2krav.util

import no.uutilsynet.testlab2krav.dto.Krav
import org.springframework.jdbc.core.DataClassRowMapper

object KravJdbcUtil {
    const val ID_PARAM: String = "id"

    const val FIND_ALL: String = "select * from krav"
    const val FIND_BY_ID: String = "select * from krav where $ID_PARAM = :$ID_PARAM"

    val kravRowmapper = DataClassRowMapper.newInstance(Krav::class.java)
}
