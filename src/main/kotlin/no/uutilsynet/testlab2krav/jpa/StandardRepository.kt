package no.uutilsynet.testlab2krav.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StandardRepository : JpaRepository<StandardEntity, Long> {
  fun findByTittel(tittel: String): StandardEntity?

  fun findById(id: Int): StandardEntity?
}
