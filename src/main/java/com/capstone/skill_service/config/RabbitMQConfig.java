package com.capstone.skill_service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${SKILL_CREATE_QUEUE}")
    private String skillCreateQueue;

    @Value("${SKILL_UPDATE_QUEUE}")
    private String skillUpdateQueue;

    @Value("${SKILL_ASSIGN_QUEUE}")
    private String skillAssignmentQueue;

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public Queue createSkillQueue() {
        return new Queue(skillCreateQueue, true);
    }

    @Bean
    public Queue updateSkillQueue() {
        return new Queue(skillUpdateQueue, true);
    }

    @Bean
    public Queue assignSkillQueue() {
        return new Queue(skillAssignmentQueue, true);
    }

}
