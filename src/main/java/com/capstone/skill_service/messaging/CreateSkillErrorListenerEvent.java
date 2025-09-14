package com.capstone.skill_service.messaging;

import lombok.RequiredArgsConstructor;
import org.common.event.ErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateSkillErrorListenerEvent {
    private static final Logger logger = LoggerFactory.getLogger(CreateSkillErrorListenerEvent.class);

    @RabbitListener(queues = "${SKILL_CREATE_ERROR_QUEUE}")
    public void consumeErrorEvent(ErrorEvent errorEvent) {
        logger.info("Consuming Error event: {}", errorEvent);
    }
}
