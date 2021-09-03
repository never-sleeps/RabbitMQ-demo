package com.kotlin.rabbitmqdemo

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class EmitController(
	@Qualifier("rabbitMqTemplate")
	private val rabbitMqTemplate: RabbitTemplate,
	private val objectMapper: ObjectMapper
) {

	@PostMapping("/emit/1")
	@ResponseStatus(HttpStatus.OK)
	fun emitToSimple(@RequestBody dto: MessageDto) {
		rabbitMqTemplate.setExchange(null)
		rabbitMqTemplate.convertAndSend(RabbitConfig.SIMPLE_QUEUE, dto.message)
	}

	@PostMapping("/emit/2")
	@ResponseStatus(HttpStatus.OK)
	fun emitToFanout(@RequestBody dto: MessageDto) {
		rabbitMqTemplate.setExchange(RabbitFanoutConfig.FANOUT_EXCHANGE)
		rabbitMqTemplate.convertAndSend(objectMapper.writeValueAsString(dto))
	}

	@PostMapping("/emit/3")
	@ResponseStatus(HttpStatus.OK)
	fun emitToDirect(@RequestBody dto: MessageDto) {
		rabbitMqTemplate.setExchange(RabbitDirectConfig.DIRECT_EXCHANGE)
		rabbitMqTemplate.convertAndSend(dto.key, dto.message)
	}

	@PostMapping("/emit/4")
	@ResponseStatus(HttpStatus.OK)
	fun emitToTopic(@RequestBody dto: MessageDto) {
		rabbitMqTemplate.setExchange(RabbitTopicConfig.TOPIC_EXCHANGE)
		rabbitMqTemplate.convertAndSend(dto.key, dto.message)
	}
}