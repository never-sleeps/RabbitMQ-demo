package com.kotlin.rabbitmqdemo

import mu.KotlinLogging
import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Component
@ConfigurationProperties("rabbit")
@Validated
class RabbitConnectionProperties {
	@NotBlank
	lateinit var host: String
	@NotBlank
	lateinit var port: String
	@NotBlank
	lateinit var username: String
	@NotBlank
	lateinit var password: String
	@NotNull
	lateinit var virtualHost: String

	var channelRpcTimeout: Int = 300000
}

@Configuration
class RabbitConfig {

	@Bean
	@Qualifier("connectionFactory")
	fun connectionFactory(
		connection: RabbitConnectionProperties
	) = CachingConnectionFactory()
		.apply {
			host = connection.host
			username = connection.username
			setPassword(connection.password)
			virtualHost = connection.virtualHost
		}

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