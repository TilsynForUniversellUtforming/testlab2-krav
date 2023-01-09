package no.uutilsynet.testlab2krav.dto

data class Paragraf(
  val id: Int,
  val nr: Int,
  val namn: String,
  val gjeldOffSektor: Boolean?,
  val gjeldPrivatSektor: Boolean?
)
