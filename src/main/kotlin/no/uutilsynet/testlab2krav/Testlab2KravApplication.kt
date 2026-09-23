package no.uutilsynet.testlab2krav

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.json.JsonMapper

@SpringBootApplication
@ConfigurationPropertiesScan
class Testlab2KravApplication {

  @Bean
  fun jsonMapper(): JsonMapper {
    return JsonMapper.builder()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .build()
  }

  @Bean
  fun jacksonJsonHttpMessageConverter(jsonMapper: JsonMapper): JacksonJsonHttpMessageConverter {
    val converter = JacksonJsonHttpMessageConverter(jsonMapper)
    converter.supportedMediaTypes =
        listOf(
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_OCTET_STREAM,
        )
    return converter
  }
}

fun main(args: Array<String>) {
  runApplication<Testlab2KravApplication>(*args)
}

@RestController
class AppNameResource {
  @GetMapping("/") fun appName() = mapOf("appName" to "testlab2-krav")
}
