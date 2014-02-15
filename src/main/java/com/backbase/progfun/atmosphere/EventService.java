/*
 * Copyright 2014 Jeanfrancois Arcand
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.backbase.progfun.atmosphere;

import java.io.IOException;

import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Ready;
import org.atmosphere.config.service.Singleton;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Atmosphere managed service.
 */
@Singleton
@ManagedService(path = "/event")
public class EventService {
    private final Logger LOGGER = LoggerFactory.getLogger(EventService.class);

    public EventService() {
        LOGGER.info("EventService instance created.");
    }

    /**
     * Invoked when the connection has been fully established and suspended, ready for receiving messages.
     * @param resource
     */
    @Ready
    public void onReady(final AtmosphereResource resource) {
        LOGGER.info("Browser {} connected.", resource.uuid());
    }

    /**
     * Invoked when the client disconnect or when an unexpected closing of the underlying connection happens.
     *
     * @param event
     */
    @Disconnect
    public void onDisconnect(AtmosphereResourceEvent event) {
        if (event.isCancelled()) {
            LOGGER.info("Browser {} unexpectedly disconnected.", event.getResource().uuid());
        } else if (event.isClosedByClient()) {
            LOGGER.info("Browser {} closed the connection.", event.getResource().uuid());
        }
    }

    /**
     * Invoked when messages are broadcasted.
     *
     * @param message
     */
    @org.atmosphere.config.service.Message(encoders = {MessageEncoder.class}, decoders = {MessageDecoder.class})
    public Message onMessage(Message message) throws IOException {
        LOGGER.info("Broadcasting new event: \"{}\"", message.getEvent());
        return message;
    }

}