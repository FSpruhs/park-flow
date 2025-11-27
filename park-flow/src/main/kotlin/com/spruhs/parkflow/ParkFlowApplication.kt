package com.spruhs.parkflow

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class ParkFlowApplication

fun main(args: Array<String>) {
    runApplication<ParkFlowApplication>(*args)
}
