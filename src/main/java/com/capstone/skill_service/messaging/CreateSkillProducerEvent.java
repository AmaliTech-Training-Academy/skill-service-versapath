package com.capstone.skill_service.messaging;

import lombok.RequiredArgsConstructor;
import org.common.event.CreateSkillEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateSkillProducerEvent {
    private static final Logger logger = LoggerFactory.getLogger(CreateSkillProducerEvent.class);
    private final RabbitTemplate rabbitTemplate;
    public void sendCreateSkillOnMoodleCommand(CreateSkillEvent createEvent) {
        logger.info("Send command to create skill structure on Moodle: {}", createEvent);

        rabbitTemplate.convertAndSend("versapath.skill.create", createEvent);
    }
}
