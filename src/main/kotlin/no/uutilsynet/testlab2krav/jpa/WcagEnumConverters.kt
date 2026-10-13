package no.uutilsynet.testlab2krav.jpa

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import no.uutilsynet.testlab2.constants.KravStatus
import no.uutilsynet.testlab2.constants.WcagPrinsipp
import no.uutilsynet.testlab2.constants.WcagRetninglinje
import no.uutilsynet.testlab2.constants.WcagSamsvarsnivaa
import no.uutilsynet.testlab2krav.findBy

@Converter
class KravStatusConverter : AttributeConverter<KravStatus, String> {
  override fun convertToDatabaseColumn(attribute: KravStatus?): String? = attribute?.status

  override fun convertToEntityAttribute(dbData: String?): KravStatus? {
    require(!dbData.isNullOrBlank()) { "KravStatus cannot be null or blank" }
    return KravStatus::status.findBy(dbData)
        ?: throw IllegalArgumentException("Unknown KravStatus value: $dbData")
  }
}

@Converter
class WcagPrinsippConverter : AttributeConverter<WcagPrinsipp, String> {
  override fun convertToDatabaseColumn(attribute: WcagPrinsipp?): String? = attribute?.prinsipp

  override fun convertToEntityAttribute(dbData: String?): WcagPrinsipp? {
    if (dbData.isNullOrBlank()) return null
    return WcagPrinsipp::prinsipp.findBy(dbData)
        ?: throw IllegalArgumentException("Unknown WcagPrinsipp value: $dbData")
  }
}

@Converter
class WcagRetninglinjeConverter : AttributeConverter<WcagRetninglinje, String> {
  override fun convertToDatabaseColumn(attribute: WcagRetninglinje?): String? =
      attribute?.retninglinje

  override fun convertToEntityAttribute(dbData: String?): WcagRetninglinje? {
    if (dbData.isNullOrBlank()) return null
    return WcagRetninglinje::retninglinje.findBy(dbData)
        ?: throw IllegalArgumentException("Unknown WcagRetninglinje value: $dbData")
  }
}

@Converter
class WcagSamsvarsnivaaConverter : AttributeConverter<WcagSamsvarsnivaa, String> {
  override fun convertToDatabaseColumn(attribute: WcagSamsvarsnivaa?): String? = attribute?.nivaa

  override fun convertToEntityAttribute(dbData: String?): WcagSamsvarsnivaa? {
    if (dbData.isNullOrBlank()) return null
    return WcagSamsvarsnivaa::nivaa.findBy(dbData)
        ?: throw IllegalArgumentException("Unknown WcagSamsvarsnivaa value: $dbData")
  }
}
