package com.example.demo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "transfer.exchange";
    public static final String QUEUE = "transfer.process";
    public static final String ROUTING_KEY = "transfer.requested";

    @Bean
    Queue transferQue(){
        return new Queue(QUEUE, true);
    }

    @Bean
    DirectExchange transferExchange(){
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    Binding transferBinding(Queue q, DirectExchange x){
        return BindingBuilder.bind(q).to(x).with(ROUTING_KEY);
    }

    @Bean
    MessageConverter jsonMessageConverter(){
        return new JacksonJsonMessageConverter();
    }
}
