package no.uutilsynet.testlab2krav.jpa

import java.util.Optional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface Wcag2KravRepository : JpaRepository<Wcag2KravEntity, Long> {

  fun findByStandard(standard: StandardEntity): List<Wcag2KravEntity>

  fun findById(id: Int): Optional<Wcag2KravEntity?>

  fun findBySuksesskriterium(suksesskriterium: String): Wcag2KravEntity

  fun save(krav: Wcag2KravEntity): Wcag2KravEntity

  fun deleteById(id: Int): Int

  fun existsById(id: Int): Boolean

  override fun findAll(): List<Wcag2KravEntity>
}
