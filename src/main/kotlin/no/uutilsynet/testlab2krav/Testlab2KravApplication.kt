package no.uutilsynet.testlab2krav

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableCaching
class Testlab2TestingApplication {

    @Bean
    fun restTemplate(restTemplateBuilder: RestTemplateBuilder): RestTemplate {
        val objectMapper =
            jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val mappingJackson2HttpMessageConverter = MappingJackson2HttpMessageConverter()
        mappingJackson2HttpMessageConverter.objectMapper = objectMapper
        mappingJackson2HttpMessageConverter.supportedMediaTypes =
            listOf(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM)

        return restTemplateBuilder
            .messageConverters(mappingJackson2HttpMessageConverter)
            .build()
    }
}

fun main(args: Array<String>) {
    runApplication<Testlab2TestingApplication>(*args)
}

@RestController
class AppNameResource {
  @GetMapping("/") fun appName() = mapOf("appName" to "testlab2-krav")
}
