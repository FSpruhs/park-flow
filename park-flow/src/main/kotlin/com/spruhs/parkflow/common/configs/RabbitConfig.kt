package com.spruhs.parkflow.common.configs

import com.spruhs.parksensormock.events.RabbitMQConstants.EXCHANGE
import com.spruhs.parksensormock.events.RabbitMQConstants.QUEUE_ARRIVED
import com.spruhs.parksensormock.events.RabbitMQConstants.QUEUE_DROVE_THROUGH
import com.spruhs.parksensormock.events.RabbitMQConstants.QUEUE_PARKED_OFF
import com.spruhs.parksensormock.events.RabbitMQConstants.QUEUE_PARKED_ON
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {
    @Bean
    fun messageConverter(): Jackson2JsonMessageConverter = Jackson2JsonMessageConverter()

    @Bean
    fun exchange(): TopicExchange = TopicExchange(EXCHANGE)

    @Bean
    fun queueArrived() = Queue(QUEUE_ARRIVED)

    @Bean
    fun queueDroveThrough() = Queue(QUEUE_DROVE_THROUGH)

    @Bean
    fun queueParkedOn() = Queue(QUEUE_PARKED_ON)

    @Bean
    fun queueParkedOff() = Queue(QUEUE_PARKED_OFF)

    @Bean
    fun bindingArrived(): Binding = BindingBuilder.bind(queueArrived()).to(exchange()).with(QUEUE_ARRIVED)

    @Bean
    fun bindingDroveThrough(): Binding =
        BindingBuilder.bind(queueDroveThrough()).to(exchange()).with(QUEUE_DROVE_THROUGH)

    @Bean
    fun bindingParkedOn(): Binding = BindingBuilder.bind(queueParkedOn()).to(exchange()).with(QUEUE_PARKED_ON)

    @Bean
    fun bindingParkedOff(): Binding = BindingBuilder.bind(queueParkedOff()).to(exchange()).with(QUEUE_PARKED_OFF)
}
