package com.backbase.progfun.atmosphere;

import java.util.Collection;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtmospherePublishService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AtmospherePublishService.class);

    private static final String SESSION_ID = "sessionId";

    public void publish(Message message) {
        BroadcasterFactory broadcasterFactory = BroadcasterFactory.getDefault();

        // could be null during bootstrapping, when Atmosphere did not initialize itself yet
        if (broadcasterFactory != null) {
            Broadcaster eventBroadcaster = broadcasterFactory.lookup("/event");
            if (eventBroadcaster != null) {

                // broadcast to the appropriate atmosphere resources.
                Collection<AtmosphereResource> atmosphereResources = eventBroadcaster.getAtmosphereResources();
                for (AtmosphereResource atmosphereResource : atmosphereResources) {
                    if (message.getSessionId().equals(getSessionId(atmosphereResource))) {
                        eventBroadcaster.broadcast(message, atmosphereResource);
                    }
                }

            }  else {
                LOGGER.error("Check your atmosphere configuration, '/event' broadcaster was not configured");
            }
        }
    }

    public String getSessionId(AtmosphereResource resource) {
        String sessionId = resource.getRequest().getHeader(SESSION_ID);
        // some browsers only support GET method with some transport and prevent setting headers.
        // As an example, the WebSocket API doesn't allow setting headers, and instead the headers will be passed
        // as a query string (attachHeadersAsQueryString is set to true by default)
        if (sessionId == null) {
            sessionId = resource.getRequest().getParameter(SESSION_ID);
        }
        if (sessionId == null) {
            LOGGER.error("Could not get the session id");
        }
        return sessionId;
    }


}
