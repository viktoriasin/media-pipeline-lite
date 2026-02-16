package ru.sinvic.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.sinvic.config.KafkaTopicConfig;
import ru.sinvic.dto.AnalyticsEventRequest;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsEventProducer {

    private final KafkaTemplate<String, AnalyticsEventRequest> kafkaTemplate;

    public void sendEvent(AnalyticsEventRequest event) {
        log.debug("Sending analytics event to Kafka: type={}, sessionId={}",
            event.eventType(), event.sessionId());

        CompletableFuture<SendResult<String, AnalyticsEventRequest>> future =
            kafkaTemplate.send(KafkaTopicConfig.ANALYTICS_EVENTS_TOPIC, event.sessionId(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Event sent successfully: offset={}, partition={}",
                    result.getRecordMetadata().offset(),
                    result.getRecordMetadata().partition());
            } else {
                log.error("Failed to send event to Kafka: sessionId={}, eventType={}",
                    event.sessionId(), event.eventType(), ex);
            }
        });
    }
}
