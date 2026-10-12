package no.uutilsynet.testlab2krav.jpa

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import org.springframework.data.relational.core.sql.In

@Entity
class StandardEntity(
    var tittel: String,
    var status: String,
    var gjeldOffentlig: Boolean,
    var gjeldPrivat: Boolean,
    @OneToMany(mappedBy = "standard", fetch = FetchType.LAZY)
    var krav: MutableList<Wcag2KravEntity> = mutableListOf(),
    @Id var id: Int? = null
)
