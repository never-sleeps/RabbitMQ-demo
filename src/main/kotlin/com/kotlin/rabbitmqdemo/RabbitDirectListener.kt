package com.kotlin.rabbitmqdemo

import com.kotlin.rabbitmqdemo.RabbitDirectConfig.Companion.ERROR_QUEUE
import com.kotlin.rabbitmqdemo.RabbitDirectConfig.Companion.SUCCESS_QUEUE
import mu.KotlinLogging
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
class RabbitDirectConfig {
	@Bean
	fun successQueue(): Queue = Queue(SUCCESS_QUEUE)

	@Bean
	fun errorQueue(): Queue = Queue(ERROR_QUEUE)

	@Bean
	fun directExchange(): DirectExchange = DirectExchange(DIRECT_EXCHANGE)

	@Bean
	fun successBinding(
		successQueue: Queue,
		directExchange: DirectExchange
	): Binding = BindingBuilder.bind(successQueue).to(directExchange).with("success")


	@Bean
	fun warningBinding(
		successQueue: Queue,
		directExchange: DirectExchange
	): Binding = BindingBuilder.bind(successQueue).to(directExchange).with("warning")

	@Bean
	fun errorBinding(
		errorQueue: Queue,
		directExchange: DirectExchange
	): Binding = BindingBuilder.bind(errorQueue).to(directExchange).with("error")

	companion object {
		const val SUCCESS_QUEUE = "direct.queue.success"
		const val ERROR_QUEUE = "direct.queue.error"

		const val DIRECT_EXCHANGE = "direct.exchange"
	}
}

@Component
class RabbitDirectListener {

	@RabbitListener(queues = [SUCCESS_QUEUE])
	fun onMessageSuccess(message: Message) {
		log.info("${message.messageProperties.consumerQueue}: $message")
	}

	@RabbitListener(queues = [ERROR_QUEUE])
	fun onMessageError(message: Message) {
		log.info("${message.messageProperties.consumerQueue}: $message")
	}

	companion object {
		val log = KotlinLogging.logger { }
	}
}