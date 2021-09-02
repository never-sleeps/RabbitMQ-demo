package com.kotlin.rabbitmqdemo

import mu.KotlinLogging
import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {

	@Bean
	@Qualifier("connectionFactory")
	fun connectionFactory() = CachingConnectionFactory()
		.apply { host = "localhost" }

	@Bean
	fun amqpAdmin(
		@Qualifier("connectionFactory") connectionFactory: CachingConnectionFactory
	): AmqpAdmin = RabbitAdmin(connectionFactory)

	@Bean
	@Qualifier("rabbitMqTemplate")
	fun rabbitTemplate(
		@Qualifier("connectionFactory") connectionFactory: ConnectionFactory
	): RabbitTemplate = RabbitTemplate(connectionFactory)

	@Bean
	fun simpleQueue(): Queue = Queue(SIMPLE_QUEUE)

	@Bean
	fun messageListenerContainer(
		@Qualifier("connectionFactory") connectionFactory: CachingConnectionFactory
	): SimpleMessageListenerContainer {
		val container = SimpleMessageListenerContainer(connectionFactory)
		container.setQueueNames(SIMPLE_QUEUE)
		container.setMessageListener { message: Message ->
			log.info("${message.messageProperties.consumerQueue}: $message")
		}
		return container
	}

	companion object {
		const val SIMPLE_QUEUE = "simple.queue"
		val log = KotlinLogging.logger { }
	}
}