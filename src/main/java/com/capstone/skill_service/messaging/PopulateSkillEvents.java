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

    @Value("${ATOM_UPDATE_TOPIC}")
    private String atomUpdateTopic;

    @Value("${ATOM_DELETE_TOPIC}")
    private String atomDeleteTopic;

    @Value("${CAPSULE_CREATE_TOPIC}")
    private String capsuleCreateTopic;

    @Value("${CAPSULE_UPDATE_TOPIC}")
    private String capsuleUpdateTopic;

    @Value("${CAPSULE_DELETE_TOPIC}")
    private String capsuleDeleteTopic;

    @Value("${CAPSULE_ASSIGN_TOPIC}")
    private String capsuleAssignTopic;

    @Value("${GROWTH_TRACK_CREATE_TOPIC}")
    private String growthTrackCreateTopic;

    @Value("${TALENT_ROUTE_CREATE_TOPIC}")
    private String talentRouteCreateTopic;

    public void populateSkillAtom(SkillAtomEvent atomEvent){
        kafkaAtomTemplate.send(atomCreateTopic, atomEvent);

        logger.info("atom event is populated: {}", atomEvent);
    }

    public void populateUpdateAtom(SkillAtomEvent atomEvent){
        kafkaAtomTemplate.send(atomUpdateTopic, atomEvent);

        logger.info("atom event is populated for update: {}", atomEvent);
    }

    public void populateDeleteAtom(SkillAtomEvent atomEvent){
        kafkaAtomTemplate.send(atomDeleteTopic, atomEvent);

        logger.info("atom event is populated for delete: {}", atomEvent);
    }

    public void populateSkillCapsule(SkillCapsuleEvent capsuleEvent){
        kafkaCapsuleTemplate.send(capsuleCreateTopic, capsuleEvent);

        logger.info("capsule event is populated: {}", capsuleEvent);
    }

    public void populateUpdateCapsule(SkillCapsuleEvent capsuleEvent){
        kafkaCapsuleTemplate.send(capsuleUpdateTopic, capsuleEvent);

        logger.info("capsule event is populated for update: {}", capsuleEvent);
    }

    public void populateDeleteCapsule(SkillCapsuleEvent capsuleEvent){
        kafkaCapsuleTemplate.send(capsuleDeleteTopic, capsuleEvent);

        logger.info("capsule event is populated for delete: {}", capsuleEvent);
    }

    public void populateAssignCapsule(SkillCapsuleEvent capsuleEvent){
        kafkaCapsuleTemplate.send(capsuleAssignTopic, capsuleEvent);

        logger.info("capsule event is populated for atom assignment: {}", capsuleEvent);
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
