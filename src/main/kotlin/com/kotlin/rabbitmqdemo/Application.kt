package com.kotlin.rabbitmqdemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RabbitmqDemoApplication

fun main(args: Array<String>) {
	runApplication<RabbitmqDemoApplication>(*args)
}