package com.capstone.skill_service.messaging;

import lombok.RequiredArgsConstructor;
import org.common.event.GrowthTrackEvent;
import org.common.event.SkillAtomEvent;
import org.common.event.SkillCapsuleEvent;
import org.common.event.TalentRouteEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopulateSkillEvents {
    private static final Logger logger = LoggerFactory.getLogger(PopulateSkillEvents.class);
    private final KafkaTemplate<String, SkillAtomEvent> kafkaAtomTemplate;
    private final KafkaTemplate<String, SkillCapsuleEvent> kafkaCapsuleTemplate;
    private final KafkaTemplate<String, GrowthTrackEvent> kafkaGrowthTrackTemplate;
    private final KafkaTemplate<String, TalentRouteEvent> kafkaTalentRouteTemplate;

    @Value("${ATOM_CREATE_TOPIC}")
    private String atomCreateTopic;

    @Value("${CAPSULE_CREATE_TOPIC}")
    private String capsuleCreateTopic;

    @Value("${GROWTH_TRACK_CREATE_TOPIC}")
    private String growthTrackCreateTopic;

    @Value("${TALENT_ROUTE_CREATE_TOPIC}")
    private String talentRouteCreateTopic;

    public void populateSkillAtom(SkillAtomEvent atomEvent){
        kafkaAtomTemplate.send(atomCreateTopic, atomEvent);

        logger.info("atom event is populated: {}", atomEvent);
    }

    public void populateSkillCapsule(SkillCapsuleEvent capsuleEvent){
        kafkaCapsuleTemplate.send(capsuleCreateTopic, capsuleEvent);

        logger.info("capsule event is populated: {}", capsuleEvent);
    }

    public void populateGrowthTrack(GrowthTrackEvent growthTrackEvent){
        kafkaGrowthTrackTemplate.send(growthTrackCreateTopic, growthTrackEvent);

        logger.info("growth track event is populated: {}", growthTrackEvent);
    }

    public void populateTalentRoute(TalentRouteEvent talentRouteEvent){
        kafkaTalentRouteTemplate.send(talentRouteCreateTopic, talentRouteEvent);

        logger.info("talent route event is populated: {}", talentRouteEvent);
    }

}
