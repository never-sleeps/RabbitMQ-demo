package com.kotlin.rabbitmqdemo

import com.kotlin.rabbitmqdemo.RabbitTopicConfig.Companion.ONE_QUEUE
import com.kotlin.rabbitmqdemo.RabbitTopicConfig.Companion.SECOND_QUEUE
import mu.KotlinLogging
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class RabbitTopicConfig {
	@Bean
	fun oneQueue(): Queue = Queue(ONE_QUEUE)

	@Bean
	fun secondQueue(): Queue = Queue(SECOND_QUEUE)

	@Bean
	fun topicExchange(): TopicExchange = TopicExchange(TOPIC_EXCHANGE)

	@Bean
	fun oneBinding(
		oneQueue: Queue,
		topicExchange: TopicExchange
	): Binding = BindingBuilder.bind(oneQueue).to(topicExchange).with("one.*")

	@Bean
	fun secondBinding(
		secondQueue: Queue,
		topicExchange: TopicExchange
	): Binding = BindingBuilder.bind(secondQueue).to(topicExchange).with("*.second")

	companion object {
		const val ONE_QUEUE = "direct.queue.one"
		const val SECOND_QUEUE = "direct.queue.second"

		const val TOPIC_EXCHANGE = "topic.exchange"
	}
}

@Component
class RabbitTopicListener {
	@RabbitListener(queues = [ONE_QUEUE])
	fun onMessage1(message: Message) {
		log.info("${message.messageProperties.consumerQueue}: $message")
	}


	@RabbitListener(queues = [SECOND_QUEUE])
	fun onMessage2(message: Message) {
		log.info("${message.messageProperties.consumerQueue}: $message")
	}

	companion object {
		val log = KotlinLogging.logger { }
	}
}