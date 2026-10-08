package no.uutilsynet.testlab2krav.testregel

import no.uutilsynet.testlab2.constants.ManuellForenklaTestregelDefinition
import no.uutilsynet.testlab2.constants.TestregelModus
import no.uutilsynet.testlab2.constants.TestregelUtfall
import no.uutilsynet.testlab2.constants.TestresultatUtfall
import no.uutilsynet.testlab2krav.dao.KravDAO
import no.uutilsynet.testlab2krav.testregel.model.InnhaldstypeTesting
import no.uutilsynet.testlab2krav.testregel.model.Tema
import no.uutilsynet.testlab2krav.testregel.model.Testobjekt
import no.uutilsynet.testlab2krav.testregel.model.Testregel
import no.uutilsynet.testlab2krav.testregel.model.TestregelInit
import org.springframework.stereotype.Service

@Service
class TestregelService(private val testregelDAO: TestregelDAO, private val kravDAO: KravDAO) {

  fun getTestregel(testregelId: Int): Testregel =
      testregelDAO.getTestregel(testregelId)
          ?: throw IllegalArgumentException("Fant ikkje testregel med id $testregelId")

  fun getTestregelByKey(testregelKey: String): Testregel =
      testregelDAO.getTestregelByTestregelId(testregelKey)
          ?: throw IllegalArgumentException("Fant ikkje testregel med nøkkel $testregelKey")

  fun getTestregelList(): List<Testregel> {
    return testregelDAO.getTestregelList()
  }

  fun createTestregel(testregelInit: TestregelInit): Int {
    return testregelDAO.createTestregel(testregelInit)
  }

  fun getTemaForTestregel(): List<Tema> {
    return testregelDAO.getTemaForTestregel()
  }

  fun getInnhaldstypeForTesting(): List<InnhaldstypeTesting> {
    return testregelDAO.getInnhaldstypeForTesting()
  }

  fun getTestobjekt(): List<Testobjekt> {
    return testregelDAO.getTestobjekt()
  }

  fun updateTestregel(testregel: Testregel): Int {
    return testregelDAO.updateTestregel(testregel)
  }

  fun deleteTestregel(testregelId: Int): Int {
    return testregelDAO.deleteTestregel(testregelId)
  }

  fun isOutcomeCustom(testregelId: Int, customUtfall: String): Boolean {
    val testregel = getTestregel(testregelId)
    val canHaveCustomOutcome = testregel.modus == TestregelModus.manuellForenkla
    if (canHaveCustomOutcome && testregel.definition is ManuellForenklaTestregelDefinition) {
      val  utfallListe = (testregel.definition as ManuellForenklaTestregelDefinition).utfall
      return utfallListe.map { it.beskrivelse }.contains(customUtfall)
    }
    return false
  }

  fun saveCustomOutcome(testregelId: Int, customUtfall: String,testresultat: TestresultatUtfall): Testregel {
    val testregel = getTestregel(testregelId)
    if (testregel.modus == TestregelModus.manuellForenkla
      && testregel.definition is ManuellForenklaTestregelDefinition) {
      val utfallListe = (testregel.definition as ManuellForenklaTestregelDefinition).utfall.toMutableList()
      val newUtfall = TestregelUtfall(
        id = null,
        beskrivelse = customUtfall,
        testresultat = testresultat,
        default = false
      )
      utfallListe.add(newUtfall)
      val newDefinition = ManuellForenklaTestregelDefinition(
        description = (testregel.definition as ManuellForenklaTestregelDefinition).description,
        helptext = (testregel.definition as ManuellForenklaTestregelDefinition).helptext,
        utfall = utfallListe
      )
      val updatedTestregel = testregel.copy(definition = newDefinition)
      updateTestregel(updatedTestregel)
      return updatedTestregel
    } else {
      error("Testregelen med id $testregelId kan ikke ha tilpasset utfall")
    }
  }

}
