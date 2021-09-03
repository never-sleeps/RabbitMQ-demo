package com.kotlin.rabbitmqdemo

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.kotlin.rabbitmqdemo.RabbitFanoutConfig.Companion.QUEUE_1
import com.kotlin.rabbitmqdemo.RabbitFanoutConfig.Companion.QUEUE_2
import mu.KotlinLogging
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.annotation.RabbitListener

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
class RabbitFanoutConfig {
	@Bean
	fun queue1(): Queue = Queue(QUEUE_1)

	@Bean
	fun queue2(): Queue = Queue(QUEUE_2)

	@Bean
	fun fanoutExchange(): FanoutExchange = FanoutExchange(FANOUT_EXCHANGE)

	@Bean
	fun binding1(
		queue1: Queue,
		fanoutExchange: FanoutExchange
	): Binding = BindingBuilder.bind(queue1).to(fanoutExchange)


	@Bean
	fun binding2(
		queue2: Queue,
		fanoutExchange: FanoutExchange
	): Binding = BindingBuilder.bind(queue2).to(fanoutExchange)


	companion object {
		const val QUEUE_1 = "fanout.queue.1"
		const val QUEUE_2 = "fanout.queue.2"
		const val FANOUT_EXCHANGE = "common.exchange"
	}
}

@Component
class RabbitFanoutListener(
	private val objectMapper: ObjectMapper
) {
	@RabbitListener(queues = [QUEUE_1])
	fun onMessage1(message: Message) {
		val dto = objectMapper.readValue<MessageDto>(String(message.body))
		log.info("received object: $dto")
		log.info("${message.messageProperties.consumerQueue}: $message")
	}


	@RabbitListener(queues = [QUEUE_2])
	fun onMessage2(message: Message) {
		val dto = objectMapper.readValue<MessageDto>(String(message.body))
		log.info("received object: $dto")
		log.info("${message.messageProperties.consumerQueue}: $message")
	}

	companion object {
		val log = KotlinLogging.logger { }
	}
}