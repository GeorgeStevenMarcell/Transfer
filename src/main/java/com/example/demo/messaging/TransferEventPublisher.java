package com.example.demo.messaging;

import com.example.demo.events.TransferRequested;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.example.demo.config.RabbitConfig.EXCHANGE;
import static com.example.demo.config.RabbitConfig.ROUTING_KEY;

@Component
public class TransferEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public TransferEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(TransferRequested event){
        rabbitTemplate.convertAndSend(EXCHANGE,ROUTING_KEY,event);
    }
}
