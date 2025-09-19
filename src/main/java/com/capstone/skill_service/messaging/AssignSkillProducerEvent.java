package com.capstone.skill_service.messaging;

import lombok.RequiredArgsConstructor;
import org.common.event.AssignAtomToCapsuleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssignSkillProducerEvent {
    @Value("${SKILL_ASSIGN_QUEUE}")
    private String skillAssignmentQueue;
    private static final Logger logger = LoggerFactory.getLogger(AssignSkillProducerEvent.class);
    private final RabbitTemplate rabbitTemplate;
    public void assignSkillOnMoodleCommand(AssignAtomToCapsuleEvent assignmentEvent) {
        logger.info("Send command event to create new lesson in a course on Moodle: {}", assignmentEvent);

        rabbitTemplate.convertAndSend(skillAssignmentQueue, assignmentEvent);
    }
}
