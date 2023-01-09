package no.uutilsynet.testlab2krav

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DatabaseAccess(val jdbcTemplate: JdbcTemplate) {

  @GetMapping("/")
  fun getDatabaseData(): String =
    jdbcTemplate
      .query("select * from Regelverk") { rs, _ -> rs.getString("namn") }
      .getOrNull(0)
      ?: "Ingen treff"
}
