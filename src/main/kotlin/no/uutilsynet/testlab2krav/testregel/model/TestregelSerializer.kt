package no.uutilsynet.testlab2krav.testregel.model

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import no.uutilsynet.testlab2.constants.ITestregelDefinition
import no.uutilsynet.testlab2.constants.ManuellForenklaTestregelDefinition
import no.uutilsynet.testlab2.constants.QualwebTestregelDefinition
import no.uutilsynet.testlab2.constants.StringTestregelDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Component
class TestregelSerializer : StdSerializer<Testregel>(Testregel::class.java) {

  override fun serialize(value: Testregel, gen: JsonGenerator, provider: SerializerProvider) {
    gen.writeStartObject()

    // Keep enum/date/null behavior aligned with the configured ObjectMapper.
    provider.defaultSerializeField("id", value.id, gen)
    provider.defaultSerializeField("testregelId", value.testregelId, gen)
    provider.defaultSerializeField("versjon", value.versjon, gen)
    provider.defaultSerializeField("namn", value.namn, gen)
    provider.defaultSerializeField("kravId", value.kravId, gen)
    provider.defaultSerializeField("status", value.status, gen)
    gen.writeStringField("datoSistEndra", value.datoSistEndra.toString())
    provider.defaultSerializeField("type", value.type, gen)
    provider.defaultSerializeField("modus", value.modus, gen)
    provider.defaultSerializeField("spraak", value.spraak, gen)
    provider.defaultSerializeField("tema", value.tema, gen)
    provider.defaultSerializeField("testobjekt", value.testobjekt, gen)
    provider.defaultSerializeField("kravTilSamsvar", value.kravTilSamsvar, gen)
    provider.defaultSerializeField("testregelSchema", value.testregelSchema, gen)
    provider.defaultSerializeField("innhaldstypeTesting", value.innhaldstypeTesting, gen)

    gen.writeFieldName("definition")
    writeDefinition(value.definition, gen, provider)

    gen.writeEndObject()
  }

  private fun writeDefinition(
    definition: ITestregelDefinition,
    gen: JsonGenerator,
    provider: SerializerProvider
  ) {
    when (definition) {
      is StringTestregelDefinition -> {
        gen.writeStartObject()
        gen.writeStringField("type", "string")
        gen.writeStringField("body", definition.body)
        gen.writeEndObject()
      }
      is QualwebTestregelDefinition -> {
        gen.writeStartObject()
        gen.writeStringField("type", "qualweb")
        gen.writeStringField("key", definition.key)
        gen.writeEndObject()
      }
      is ManuellForenklaTestregelDefinition -> {
        gen.writeStartObject()
        gen.writeStringField("type", "manuell-forenkla")
        gen.writeStringField("description", definition.description)
        provider.defaultSerializeField("utfall", definition.utfall, gen)
        gen.writeEndObject()
      }
      else -> provider.defaultSerializeValue(definition, gen)
    }
  }
}

@Configuration
class JacksonCustomConfig {
  @Bean
  fun statusModule(): SimpleModule {
    val module = SimpleModule()
    module.addSerializer<Testregel>(Testregel::class.java, TestregelSerializer())
    return module
  }
}
