package no.uutilsynet.testlab2krav.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching // Triggers post-processors to scan for caching annotations
public class CacheConfig {}