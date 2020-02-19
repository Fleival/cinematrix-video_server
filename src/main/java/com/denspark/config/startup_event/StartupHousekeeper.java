package com.denspark.config.startup_event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class StartupHousekeeper {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @EventListener(ContextRefreshedEvent.class)
    public void contextRefreshedEvent() {
        LOGGER.debug("StartupHousekeeper event invoked!!!!!");
    }
}