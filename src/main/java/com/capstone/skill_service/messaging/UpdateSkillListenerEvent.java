package com.capstone.skill_service.messaging;

import com.capstone.skill_service.service.CapsuleService;
import lombok.RequiredArgsConstructor;
import org.common.event.UpdateSkillEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateSkillListenerEvent {
    private static final Logger logger = LoggerFactory.getLogger(UpdateSkillListenerEvent.class);
    private final CapsuleService capsuleService;

    @RabbitListener(queues = "${SKILL_UPDATE_QUEUE}")
    public void handleMoodleUserCreation(UpdateSkillEvent updateSkillEvent) {
        logger.info("Start updating skills: {}", updateSkillEvent);

       capsuleService.updateSkillWithMoodleData(updateSkillEvent);

    }
}
