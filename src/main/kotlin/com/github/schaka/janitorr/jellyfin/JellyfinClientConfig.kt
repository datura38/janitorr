package com.github.schaka.janitorr.jellyfin

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import feign.Feign
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.client.RestTemplate

@Configuration
class JellyfinClientConfig {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }

    @Jellyfin
    @Bean
    fun jellyfinRestTemplate(builder: RestTemplateBuilder, properties: JellyfinProperties): RestTemplate {
        return builder
            .rootUri("${properties.url}/")
            .defaultHeader(AUTHORIZATION, "MediaBrowser Token=\"${properties.apiKey}\", Client=\"Janitorr\", Version=\"1.0\"")
            .build()
    }

    @Bean
    fun jellyfinClient(properties: JellyfinProperties, mapper: ObjectMapper): JellyfinClient {
        return Feign.builder()
                .decoder(JacksonDecoder(mapper))
                .encoder(JacksonEncoder(mapper))
                .requestInterceptor {
                    it.header(AUTHORIZATION, "MediaBrowser Token=\"${properties.apiKey}\", Client=\"Janitorr\", Version=\"1.0\"")
                    it.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                }
                .target(JellyfinClient::class.java, properties.url)
    }

}