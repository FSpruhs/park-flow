package com.spruhs.parkflowsimulator.applicationrunner

import com.spruhs.parkflowsimulator.getLogger
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Component
@Order(1)
class DatabaseCleaner(
    private val mongoTemplate: MongoTemplate,
    private val jdbcTemplate: JdbcTemplate,
    private val webClient: WebClient
) : ApplicationRunner {

    private val log = getLogger(javaClass)

    override fun run(args: ApplicationArguments?) {
        log.info("----- Start cleaning mongo collections -----")
        listOf(
            "parking_spot_catalog",
            "plate_numbers",
            "gates",
            "parking_spots",
            "customers",
            "vehicle_history",
            "invoices"
        ).forEach { collection ->
            log.info("Cleaning mongo collection $collection")
            mongoTemplate.remove(Query(), collection)
        }

        log.info("----- Start cleaning postgres tables -----")
        jdbcTemplate.execute(
            "TRUNCATE TABLE parkflow.snapshots, parkflow.events RESTART IDENTITY CASCADE"
        )

        log.info("----- Start cleaning caches -----")
        webClient.delete()
            .uri("/parking-operation/cache")
            .retrieve()
            .bodyToMono<Unit>()
            .block()
    }
}
