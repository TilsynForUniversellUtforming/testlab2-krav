package no.uutilsynet.testlab2krav.testregel.model

interface ITestregelDefinition

class StringTestregelDefinition(val body: String) : ITestregelDefinition

class QualwebTestregelDefinition(val key: String) : ITestregelDefinition

class ManuellForenklaTestregelDefinition(
  val description: String,
  val utfall: List<TestregelUtfall>
) : ITestregelDefinition
