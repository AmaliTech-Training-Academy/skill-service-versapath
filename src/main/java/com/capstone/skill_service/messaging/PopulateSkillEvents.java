package com.capstone.skill_service.messaging;

import lombok.RequiredArgsConstructor;
import org.common.event.GrowthTrackEvent;
import org.common.event.SkillAtomEvent;
import org.common.event.SkillCapsuleEvent;
import org.common.event.TalentRouteEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopulateSkillEvents {
    private final KafkaTemplate<String, SkillAtomEvent> kafkaAtomTemplate;
    private final KafkaTemplate<String, SkillCapsuleEvent> kafkaCapsuleTemplate;
    private final KafkaTemplate<String, GrowthTrackEvent> kafkaGrowthTrackTemplate;
    private final KafkaTemplate<String, TalentRouteEvent> kafkaTalentRouteTemplate;

    public void populateSkillAtom(SkillAtomEvent atomEvent){
        kafkaAtomTemplate.send("atom.create", atomEvent);
    }

    public void populateSkillCapsule(SkillCapsuleEvent capsuleEvent){
        kafkaCapsuleTemplate.send("capsule.create", capsuleEvent);
    }

    public void populateGrowthTrack(GrowthTrackEvent growthTrackEvent){
        kafkaGrowthTrackTemplate.send("growthTrack.create", growthTrackEvent);
    }

    public void populateTalentRoute(TalentRouteEvent talentRouteEvent){
        kafkaTalentRouteTemplate.send("talentRoute.create", talentRouteEvent);
    }

}
