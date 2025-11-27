package com.spruhs.parkflowsimulator.applicationrunner

import com.spruhs.parkflowsimulator.getLogger
import com.spruhs.parkflowsimulator.scenario.Scenario
import kotlinx.coroutines.runBlocking
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(2)
class ScenarioExecutor(
    private val context: ApplicationContext,
    private val scenario: Scenario,
) : ApplicationRunner {

    private val log = getLogger(javaClass)

    override fun run(args: ApplicationArguments) {

        val job = scenario.run()

        log.info("------ Start scenario ${scenario.javaClass.simpleName} ------")
        runBlocking { job.join() }
        log.info("------ Scenario ${scenario.javaClass.simpleName} finished and validated, shutting down ------")

        SpringApplication.exit(context)
    }
}
