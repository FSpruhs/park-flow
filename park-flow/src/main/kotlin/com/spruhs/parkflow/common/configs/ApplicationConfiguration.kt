package com.spruhs.parkflow.common.configs

import com.spruhs.parkflow.common.helper.getLogger
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfiguration {
    private val log = getLogger(javaClass)
    private val applicationCoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Bean
    fun applicationScope(): CoroutineScope = applicationCoroutineScope

    @PreDestroy
    fun shutdown() {
        log.info("Shutting down application scope")
        applicationCoroutineScope.cancel()
    }
}
