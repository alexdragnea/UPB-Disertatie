package ro.upb.iotbridgeservice.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static ro.upb.common.constant.KafkaConstants.IOT_EVENT_TOPIC;

@Configuration
public class KafkaTopic {

    @Bean
    public NewTopic IotEventTopic() {
        return TopicBuilder.name(IOT_EVENT_TOPIC).build();
    }
}