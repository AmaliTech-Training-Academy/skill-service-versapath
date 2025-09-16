package com.capstone.skill_service.messaging;

import lombok.RequiredArgsConstructor;
import org.common.event.ErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateSkillErrorListenerEvent {
    private static final Logger logger = LoggerFactory.getLogger(CreateSkillErrorListenerEvent.class);
    private final SimpMessagingTemplate messagingTemplate;
    @RabbitListener(queues = "${SKILL_CREATE_ERROR_QUEUE}")
    public void consumeErrorEvent(ErrorEvent errorEvent) {
        // send error message to the client
        messagingTemplate.convertAndSend("/topic/errors", errorEvent);

        logger.info("Consuming Error event: {}", errorEvent);
    }
}
