package com.backbase.progfun.atmosphere;

import static com.backbase.progfun.atmosphere.Event.ACCOUNT_CREDITED;
import static com.backbase.progfun.atmosphere.Event.ACCOUNT_DEBITED;
import static com.backbase.progfun.atmosphere.Event.ACCOUNT_LOCKED;

import java.util.Random;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

/**
 * Example processor providing two random messages.
 */
public class AccountBalanceProcessor implements Processor {

    private Random randomGenerator = new Random();

    @Override
    public void process(Exchange exchange) throws Exception {

        Message out = exchange.getOut();

        int id = randomGenerator.nextInt(3);
        if (id == 0) {
            out.setBody(new com.backbase.progfun.atmosphere.Message(ACCOUNT_CREDITED, String.valueOf(id)));
        } else if (id == 1) {
            out.setBody(new com.backbase.progfun.atmosphere.Message(ACCOUNT_DEBITED, String.valueOf(id)));
        } else if (id == 2) {
            out.setBody(new com.backbase.progfun.atmosphere.Message(ACCOUNT_LOCKED, String.valueOf(id)));
        }
    }
}
