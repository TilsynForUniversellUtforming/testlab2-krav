package no.uutilsynet.testlab2krav.config

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.module.kotlin.KotlinModule

@Configuration
class JacksonConfig {

  @Bean
  fun kotlinJsonMapperCustomizer(): JsonMapperBuilderCustomizer {
    return JsonMapperBuilderCustomizer { builder ->
      // Configure KotlinModule for Jackson 3.x to handle Kotlin default parameter values
      builder.addModule(KotlinModule.Builder().withReflectionCacheSize(512).build())
    }
  }
}
