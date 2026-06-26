package no.uutilsynet.testlab2krav.testregel

import com.fasterxml.jackson.databind.ObjectMapper
import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit
import no.uutilsynet.testlab2.constants.TestlabLocale
import no.uutilsynet.testlab2.constants.TestregelInnholdstype
import no.uutilsynet.testlab2.constants.TestregelModus
import no.uutilsynet.testlab2.constants.TestregelStatus
import no.uutilsynet.testlab2.constants.TestresultatUtfall
import no.uutilsynet.testlab2krav.testregel.TestregelDAO.TestregelParams.deleteTestregelSql
import no.uutilsynet.testlab2krav.testregel.TestregelDAO.TestregelParams.getTestregelByTestregelId
import no.uutilsynet.testlab2krav.testregel.TestregelDAO.TestregelParams.getTestregelListByIdList
import no.uutilsynet.testlab2krav.testregel.TestregelDAO.TestregelParams.getTestregelListSql
import no.uutilsynet.testlab2krav.testregel.TestregelDAO.TestregelParams.getTestregelSql
import no.uutilsynet.testlab2krav.testregel.TestregelDAO.TestregelParams.updateTestregel
import no.uutilsynet.testlab2krav.testregel.model.InnhaldstypeTesting
import no.uutilsynet.testlab2krav.testregel.model.ManuellForenklaTestregelDefinition
import no.uutilsynet.testlab2krav.testregel.model.StringTestregelDefinition
import no.uutilsynet.testlab2krav.testregel.model.Tema
import no.uutilsynet.testlab2krav.testregel.model.Testobjekt
import no.uutilsynet.testlab2krav.testregel.model.Testregel
import no.uutilsynet.testlab2krav.testregel.model.TestregelInit
import no.uutilsynet.testlab2krav.testregel.model.TestregelUtfall
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.dao.support.DataAccessUtils
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class TestregelDAO(
  val jdbcTemplate: NamedParameterJdbcTemplate,
  private val objectMapper: ObjectMapper
) {

  object TestregelParams {

    val getTestregelListSql =
      """select id, testregel_id,versjon,namn, krav_id, status, dato_sist_endra,type , modus ,spraak,tema,testobjekt,krav_til_samsvar,testregel_schema, innhaldstype_testing  
        |from "testlab2_krav"."testregel" order by id"""
        .trimMargin()

    val getTestregelListByIdList =
      """select id, testregel_id,versjon,namn, krav_id, status, dato_sist_endra,type , modus ,spraak,tema,testobjekt,krav_til_samsvar,testregel_schema, innhaldstype_testing  
        |from "testlab2_krav"."testregel" where id in (:ids) order by id"""
        .trimMargin()

    val getTestregelSql =
      """select id, testregel_id,versjon,namn, krav_id, status, dato_sist_endra,type, modus ,spraak,tema,testobjekt,krav_til_samsvar,testregel_schema, innhaldstype_testing 
        |from "testlab2_krav"."testregel" where id = :id order by id"""
        .trimMargin()

    val getTestregelByTestregelId =
      """select id, testregel_id,versjon,namn, krav_id, status, dato_sist_endra,type, modus ,spraak,tema,testobjekt,krav_til_samsvar,testregel_schema, innhaldstype_testing 
        |from "testlab2_krav"."testregel" 
        |where testregel_id = :testregelId 
        |and versjon=(
        |select max(versjon) 
        |from testlab2_krav.testregel where testregel_id= :testregelId) order by id limit 1"""
        .trimMargin()

    val deleteTestregelSql =
      """delete from "testlab2_krav"."testregel" where id = :id""".trimMargin()

    val getTestregelUtfallForTestregelSql =
      """select id, utfall, erdefault
        |from "testlab2_krav"."testregel_utfall"
        |where testregel_id = :testregelId
        |order by id"""
        .trimMargin()

    val deleteTestregelUtfallForTestregelSql =
      """delete from "testlab2_krav"."testregel_utfall" where testregel_id = :testregelId"""

    val getNextTestregelUtfallIdSql =
      "select coalesce(max(id), 0) + 1 from \"testlab2_krav\".\"testregel_utfall\""

    val insertTestregelUtfallSql =
      """insert into "testlab2_krav"."testregel_utfall" (id, testregel_id, utfall, erdefault)
        |values (:id, :testregelId, :utfall, :erDefault)"""
        .trimMargin()

    val updateTestregel =
      """ update "testlab2_krav"."testregel" set namn = :namn, testregel_id = :testregel_id,krav_id = :krav_id, versjon = :versjon,status = :status, dato_sist_endra = :dato_sist_endra, type = :type, modus = :modus,
                spraak = :spaak, tema = :tema, testobjekt = :testobjekt, krav_til_samsvar = :krav_til_samsvar , testregel_schema = :testregel_schema, innhaldstype_testing = :innhaldstype_testing 
                where id = :id"""
        .trimMargin()
  }

  private val testregelRowMapper =
    RowMapper<Testregel> { rs, _ ->
      val schema = rs.getString("testregel_schema")
      val id = rs.getInt("id")
      val modus = parseModus(rs.getString("modus"))

      val definition =
        if (modus == TestregelModus.manuellForenkla) {
          ManuellForenklaTestregelDefinition(
            description = schema, utfall = getTestregelUtfallForTestregel(id))
        } else {
          StringTestregelDefinition(schema)
        }

      Testregel(
        id = id,
        testregelId = rs.getString("testregel_id"),
        versjon = rs.getInt("versjon"),
        namn = rs.getString("namn"),
        kravId = rs.getInt("krav_id"),
        status = parseStatus(rs.getString("status")),
        datoSistEndra = rs.getTimestamp("dato_sist_endra").toInstant(),
        type = parseInnholdstype(rs.getString("type")),
        modus = modus,
        spraak = parseSpraak(rs.getString("spraak")),
        tema = rs.getNullableInt("tema"),
        testobjekt = rs.getNullableInt("testobjekt"),
        kravTilSamsvar = rs.getString("krav_til_samsvar"),
        testregelSchema = schema,
        innhaldstypeTesting = rs.getNullableInt("innhaldstype_testing"),
        definition = definition)
    }

  @Cacheable("testregel", unless = "#result==null")
  fun getTestregel(id: Int): Testregel? =
    DataAccessUtils.singleResult(
      jdbcTemplate.query(getTestregelSql, mapOf("id" to id), testregelRowMapper))

  @Cacheable("testregelar", unless = "#result.isEmpty()")
  fun getTestregelList(): List<Testregel> =
    jdbcTemplate.query(getTestregelListSql, testregelRowMapper)

  @Cacheable("testregelByTestregelId", unless = "#result==null")
  fun getTestregelByTestregelId(testregelId: String): Testregel? =
    DataAccessUtils.singleResult(
      jdbcTemplate.query(
        getTestregelByTestregelId, mapOf("testregelId" to testregelId), testregelRowMapper))

  fun getMany(testregelIdList: List<Int>): List<Testregel> =
    jdbcTemplate.query(
      getTestregelListByIdList, mapOf("ids" to testregelIdList), testregelRowMapper)

  @Transactional
  @CacheEvict(
    value =
      [
        "testregel",
        "testregelByTestregelId",
        "testregelar",
        "regelsett",
        "regelsettlist",
        "regelsettlistbase"],
    allEntries = true)
  fun createTestregel(testregelInit: TestregelInit): Int {

    val keyHolder: KeyHolder = GeneratedKeyHolder()

    val params = MapSqlParameterSource()
    params.addValue("krav_id", testregelInit.kravId)
    params.addValue("testregel_schema", testregelInit.testregelSchema)
    params.addValue("namn", testregelInit.namn)
    params.addValue("modus", testregelInit.modus.value)
    params.addValue("testregel_id", setTestregelId(testregelInit))
    params.addValue("versjon", 1)
    params.addValue("status", testregelInit.status.value)
    params.addValue(
      "dato_sist_endra",
      Timestamp.from(testregelInit.datoSistEndra.truncatedTo(ChronoUnit.MINUTES)))
    params.addValue("spraak", testregelInit.spraak.value)
    params.addValue("tema", testregelInit.tema)
    params.addValue("type", testregelInit.type.value)
    params.addValue("testobjekt", testregelInit.testobjekt)
    params.addValue("krav_til_samsvar", testregelInit.kravTilSamsvar)
    params.addValue("innhaldstype_testing", testregelInit.innhaldstypeTesting)

    jdbcTemplate.update(
      """
          insert into 
            "testlab2_krav"."testregel"(
              krav_id,
              testregel_schema,
              namn,
              modus,
              testregel_id,
              versjon,
              status,
              dato_sist_endra,
              spraak,
              tema,
              type,
              testobjekt,
              krav_til_samsvar,
              innhaldstype_testing
            ) values (
              :krav_id,
              :testregel_schema,
              :namn,
              :modus,
              :testregel_id,
              :versjon,
              :status,
              :dato_sist_endra,
              :spraak,
              :tema,
              :type,
              :testobjekt,
              :krav_til_samsvar,
              :innhaldstype_testing
            ) 
        """
        .trimIndent(),
      params,
      keyHolder)

    val testregelId = keyHolder.keys?.get("id") as Int
    if (testregelInit.modus == TestregelModus.manuellForenkla) {
      replaceTestregelUtfall(testregelId, extractUtfallFromSchema(testregelInit.testregelSchema))
    }

    return testregelId
  }

  @Transactional
  @CacheEvict(
    cacheNames =
      [
        "testregel",
        "testregelByTestregelId",
        "testregelar",
        "regelsett",
        "regelsettlist",
        "regelsettlistbase"],
    allEntries = true)
  fun updateTestregel(testregel: Testregel) =
    jdbcTemplate
      .update(
        updateTestregel,
        mapOf(
          "id" to testregel.id,
          "testregel_id" to testregel.testregelId,
          "versjon" to testregel.versjon,
          "namn" to testregel.namn,
          "krav_id" to testregel.kravId,
          "status" to testregel.status.value,
          "dato_sist_endra" to Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MINUTES)),
          "type" to testregel.type.value,
          "modus" to testregel.modus.value,
          "spaak" to testregel.spraak.value,
          "tema" to testregel.tema,
          "testobjekt" to testregel.testobjekt,
          "krav_til_samsvar" to testregel.kravTilSamsvar,
          "testregel_schema" to testregel.testregelSchema,
          "innhaldstype_testing" to (testregel.innhaldstypeTesting)))
      .also {
        if (testregel.modus == TestregelModus.manuellForenkla) {
          replaceTestregelUtfall(testregel.id, extractUtfallForDefinition(testregel))
        } else {
          deleteTestregelUtfallForTestregel(testregel.id)
        }
      }

  @Transactional
  @CacheEvict(
    cacheNames =
      [
        "testregel",
        "testregelByTestregelId",
        "testregelar",
        "regelsett",
        "regelsettlist",
        "regelsettlistbase"],
    allEntries = true)
  fun deleteTestregel(testregelId: Int) =
    deleteTestregelUtfallForTestregel(testregelId).let {
      jdbcTemplate.update(deleteTestregelSql, mapOf("id" to testregelId))
    }

  fun setTestregelId(testregelInit: TestregelInit): String {
    return if (testregelInit.modus == TestregelModus.automatisk) {
      testregelInit.testregelSchema
    } else testregelInit.testregelId
  }

  @Cacheable("innhaldstypeForTesting")
  fun getInnhaldstypeForTesting(): List<InnhaldstypeTesting> =
    jdbcTemplate.query(
      """select id, innhaldstype from "innhaldstype_testing"""",
      DataClassRowMapper.newInstance(InnhaldstypeTesting::class.java))

  fun getTemaForTestregel(): List<Tema> =
    jdbcTemplate.query("""select * from "tema"""", DataClassRowMapper.newInstance(Tema::class.java))

  @Cacheable("testobjekt")
  fun getTestobjekt(): List<Testobjekt> =
    jdbcTemplate.query(
      """select * from "testobjekt"""", DataClassRowMapper.newInstance(Testobjekt::class.java))

  fun getTestregelForKrav(kravId: Int): List<Testregel> =
    jdbcTemplate.query(
      """select * from testregel where krav_id = :kravId""",
      mapOf("kravId" to kravId),
      testregelRowMapper)

  fun createInnholdstypeTesting(innholdstypeTesting: String): Int {
    return jdbcTemplate.update(
      """insert into "innhaldstype_testing" (innhaldstype) values (:innhaldstype_testing)""",
      mapOf("innhaldstype_testing" to innholdstypeTesting))
  }

  fun createTema(tema: String): Int {
    val keyHolder = GeneratedKeyHolder()

    val params = MapSqlParameterSource()
    params.addValue("tema", tema)

    jdbcTemplate.update("""insert into "tema" (tema) values (:tema)""", params, keyHolder)

    return keyHolder.keys?.get("id") as Int
  }

  private fun parseStatus(value: String): TestregelStatus =
    TestregelStatus.entries.firstOrNull { it.value == value || it.name == value }
      ?: error("Unknown testregel status: $value")

  private fun parseModus(value: String): TestregelModus =
    TestregelModus.entries.firstOrNull { it.value == value || it.name == value }
      ?: error("Unknown testregel modus: $value")

  private fun parseInnholdstype(value: String): TestregelInnholdstype =
    TestregelInnholdstype.entries.firstOrNull { it.value == value || it.name == value }
      ?: error("Unknown testregel innholdstype: $value")

  private fun parseSpraak(value: String): TestlabLocale =
    TestlabLocale.entries.firstOrNull { it.value == value || it.name == value }
      ?: error("Unknown testregel spraak: $value")

  private fun java.sql.ResultSet.getNullableInt(column: String): Int? {
    val value = getInt(column)
    return if (wasNull()) null else value
  }

  private fun getTestregelUtfallForTestregel(testregelId: Int): List<TestregelUtfall> =
    jdbcTemplate.query(
      TestregelParams.getTestregelUtfallForTestregelSql, mapOf("testregelId" to testregelId)) {
        rs,
        _ ->
        toTestregelUtfall(rs.getInt("id"), rs.getString("utfall"), rs.getBoolean("erdefault"))
      }

  private fun replaceTestregelUtfall(testregelId: Int, utfall: List<TestregelUtfall>) {
    deleteTestregelUtfallForTestregel(testregelId)
    utfall.forEach { createTestregelUtfall(testregelId, it) }
  }

  private fun deleteTestregelUtfallForTestregel(testregelId: Int): Int =
    jdbcTemplate.update(
      TestregelParams.deleteTestregelUtfallForTestregelSql, mapOf("testregelId" to testregelId))

  private fun createTestregelUtfall(testregelId: Int, utfall: TestregelUtfall): Int {
    val id =
      jdbcTemplate.queryForObject(
        TestregelParams.getNextTestregelUtfallIdSql, emptyMap<String, Any>(), Int::class.java)
        ?: error("Unable to allocate id for testregel_utfall")

    return jdbcTemplate.update(
      TestregelParams.insertTestregelUtfallSql,
      mapOf(
        "id" to id,
        "testregelId" to testregelId,
        "utfall" to serializeUtfall(utfall),
        "erDefault" to utfall.default))
  }

  private fun extractUtfallForDefinition(testregel: Testregel): List<TestregelUtfall> {
    val manuellDefinition = testregel.definition as? ManuellForenklaTestregelDefinition
    return manuellDefinition?.utfall ?: extractUtfallFromSchema(testregel.testregelSchema)
  }

  private fun extractUtfallFromSchema(schema: String): List<TestregelUtfall> =
    runCatching {
        val root = objectMapper.readTree(schema)
        val utfallNodes = root.get("utfall") ?: return emptyList()
        if (!utfallNodes.isArray) return emptyList()

        utfallNodes.mapIndexed { index, node ->
          val beskrivelse =
            node.get("beskrivelse")?.asText()
              ?: node.get("description")?.asText() ?: node.get("utfall")?.asText() ?: ""

          val testresultat =
            parseUtfall(node.get("testresultat")?.asText() ?: node.get("resultat")?.asText())

          TestregelUtfall(
            id = node.get("id")?.asInt() ?: index,
            beskrivelse = beskrivelse,
            testresultat = testresultat,
            default = node.get("default")?.asBoolean()
                ?: node.get("erDefault")?.asBoolean() ?: false)
        }
      }
      .getOrDefault(emptyList())

  private fun serializeUtfall(utfall: TestregelUtfall): String =
    objectMapper.writeValueAsString(
      mapOf(
        "beskrivelse" to utfall.beskrivelse,
        "testresultat" to utfall.testresultat.name,
        "default" to utfall.default))

  private fun toTestregelUtfall(id: Int, utfallValue: String, erDefault: Boolean): TestregelUtfall {
    val parsed = runCatching { objectMapper.readTree(utfallValue) }.getOrNull()

    val beskrivelse =
      parsed?.get("beskrivelse")?.asText()
        ?: parsed?.get("description")?.asText() ?: parsed?.get("utfall")?.asText() ?: utfallValue

    val testresultat =
      parseUtfall(parsed?.get("testresultat")?.asText() ?: parsed?.get("resultat")?.asText())

    return TestregelUtfall(
      id = id, beskrivelse = beskrivelse, testresultat = testresultat, default = erDefault)
  }

  private fun parseUtfall(value: String?): TestresultatUtfall =
    TestresultatUtfall.entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
      ?: TestresultatUtfall.ikkjeTesta
}
