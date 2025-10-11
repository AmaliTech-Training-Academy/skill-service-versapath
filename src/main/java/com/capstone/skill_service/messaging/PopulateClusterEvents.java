package com.capstone.skill_service.messaging;

import lombok.RequiredArgsConstructor;
import org.common.event.ClusterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopulateClusterEvents {
    private static final Logger logger = LoggerFactory.getLogger(PopulateClusterEvents.class);
    private final KafkaTemplate<String, ClusterEvent> kafkaClusterTemplate;

    @Value("${CLUSTER_CREATE_TOPIC}")
    private String clusterCreateTopic;

    public void populateCreateCluster(ClusterEvent clusterEvent){
        kafkaClusterTemplate.send(clusterCreateTopic, clusterEvent);

        logger.info("create cluster event is populated: {}", clusterEvent);
    }

}
