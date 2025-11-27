package com.spruhs.parkflow.common.configs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class ParkflowWebClientConfig {
    @Bean
    fun parkflowWebClient(): WebClient =
        WebClient.builder()
            .baseUrl("http://localhost:8081/api/v1")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
}
