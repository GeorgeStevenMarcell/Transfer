package com.example.demo.messaging;

import com.example.demo.config.RabbitConfig;
import com.example.demo.events.TransferRequested;
import com.example.demo.services.PaymentProviderSimulator;
import com.example.demo.services.ProviderResult;
import com.example.demo.services.TransferService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;


@Component
public class TransferWorker {

    @Autowired
    PaymentProviderSimulator providerSimulator;

    @Autowired
    TransferService transferService;

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void handle(TransferRequested event,
                       Channel channel,
                       @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws Exception {
        try {
            if(!transferService.isPending(event.transactionId())) return;
            ProviderResult result = providerSimulator.call(event);

            if(result.success()){
                    transferService.markSuccess(event.transactionId());
            }
            else{
                transferService.markFailedAndRefund(event.transactionId(), result.failureReason());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            channel.basicAck(tag, false);
        }
    }
}
