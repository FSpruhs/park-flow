package com.spruhs.parkflowsimulator

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class ParkFlowSimulatorApplication

fun main(args: Array<String>) {
	runApplication<ParkFlowSimulatorApplication>(*args)
}

fun getLogger(forClass: Class<*>): Logger = LoggerFactory.getLogger(forClass)