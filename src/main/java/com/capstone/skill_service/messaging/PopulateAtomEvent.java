package com.capstone.skill_service.messaging;

import lombok.RequiredArgsConstructor;
import org.common.event.TestEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopulateAtomEvent {
    private final KafkaTemplate<String, TestEvent> kafkaTemplate;

    public void populateSkillAtom(TestEvent testEvent){
        kafkaTemplate.send("user_create", testEvent);
    }

}
