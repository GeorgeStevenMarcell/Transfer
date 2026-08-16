package com.example.demo.services;

import com.example.demo.events.TransferRequested;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class PaymentProviderSimulator {
    private static final double  failureRate = 0.10;
    private static final Long  minDelayMs = 200L;
    private static final Long  maxDelayMs = 2000L;

    public ProviderResult call(TransferRequested event) throws InterruptedException {
        Thread.sleep(ThreadLocalRandom.current().nextLong(minDelayMs,maxDelayMs+1));
        if(ThreadLocalRandom.current().nextDouble() < failureRate)
            return new ProviderResult(false,"Error Reason");
        return new ProviderResult(true,null);
    }
}
