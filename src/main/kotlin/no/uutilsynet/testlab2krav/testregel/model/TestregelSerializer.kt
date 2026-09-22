package no.uutilsynet.testlab2krav.testregel.model

import no.uutilsynet.testlab2.constants.ITestregelDefinition
import no.uutilsynet.testlab2.constants.ManuellForenklaTestregelDefinition
import no.uutilsynet.testlab2.constants.QualwebTestregelDefinition
import no.uutilsynet.testlab2.constants.StringTestregelDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import tools.jackson.core.JsonGenerator
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.module.SimpleModule
import tools.jackson.databind.ser.std.StdSerializer

@Component
class TestregelSerializer : StdSerializer<Testregel>(Testregel::class.java) {

  override fun serialize(value: Testregel, gen: JsonGenerator, provider: SerializationContext) {
    gen.writeStartObject()

    // Keep enum/date/null behavior aligned with the configured ObjectMapper.
    provider.defaultSerializeProperty("id", value.id, gen)
    provider.defaultSerializeProperty("testregelId", value.testregelId, gen)
    provider.defaultSerializeProperty("versjon", value.versjon, gen)
    provider.defaultSerializeProperty("namn", value.namn, gen)
    provider.defaultSerializeProperty("kravId", value.kravId, gen)
    provider.defaultSerializeProperty("status", value.status, gen)
    gen.writeStringProperty("datoSistEndra", value.datoSistEndra.toString())
    provider.defaultSerializeProperty("type", value.type, gen)
    provider.defaultSerializeProperty("modus", value.modus, gen)
    provider.defaultSerializeProperty("spraak", value.spraak, gen)
    provider.defaultSerializeProperty("tema", value.tema, gen)
    provider.defaultSerializeProperty("testobjekt", value.testobjekt, gen)
    provider.defaultSerializeProperty("kravTilSamsvar", value.kravTilSamsvar, gen)
    provider.defaultSerializeProperty("testregelSchema", value.testregelSchema, gen)
    provider.defaultSerializeProperty("innhaldstypeTesting", value.innhaldstypeTesting, gen)

    gen.writeName("definition")
    writeDefinition(value.definition, gen, provider)

    gen.writeEndObject()
  }

  private fun writeDefinition(
      definition: ITestregelDefinition,
      gen: JsonGenerator,
      provider: SerializationContext,
  ) {
    when (definition) {
      is StringTestregelDefinition -> {
        gen.writeStartObject()
        gen.writeStringProperty("type", "string")
        gen.writeStringProperty("body", definition.body)
        gen.writeEndObject()
      }
      is QualwebTestregelDefinition -> {
        gen.writeStartObject()
        gen.writeStringProperty("type", "qualweb")
        gen.writeStringProperty("key", definition.key)
        gen.writeEndObject()
      }
      is ManuellForenklaTestregelDefinition -> {
        gen.writeStartObject()
        gen.writeStringProperty("type", "manuell-forenkla")
        gen.writeStringProperty("description", definition.description)
        provider.defaultSerializeProperty("utfall", definition.utfall, gen)
        gen.writeEndObject()
      }
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
