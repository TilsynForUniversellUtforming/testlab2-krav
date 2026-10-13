package no.uutilsynet.testlab2krav.jpa

import jakarta.persistence.Id
import no.uutilsynet.testlab2.constants.KravStatus
import jakarta.persistence.Entity
import jakarta.persistence.Convert
import jakarta.persistence.ManyToMany
import jakarta.persistence.FetchType

@Entity(name = "standard")
class StandardEntity(
    var tittel: String,
    @Convert(converter = KravStatusConverter::class)
    var status: KravStatus,
    var gjeldOffentlig: Boolean,
    var gjeldPrivat: Boolean,
    @ManyToMany(mappedBy = "standard", fetch = FetchType.LAZY)
    var krav: MutableSet<Wcag2KravEntity> = mutableSetOf(),
    @Id var id: Int? = null,
)
