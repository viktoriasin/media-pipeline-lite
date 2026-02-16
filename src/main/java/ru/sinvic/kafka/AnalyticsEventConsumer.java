package ru.sinvic.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import ru.sinvic.config.KafkaTopicConfig;
import ru.sinvic.dto.AnalyticsEventRequest;
import ru.sinvic.service.AnalyticsService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsEventConsumer {

    private final AnalyticsService analyticsService;

    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2),
        kafkaTemplate = "kafkaTemplate",
        dltStrategy = DltStrategy.NO_DLT // риск потери данных, сделано для упрощения, в идеале проставить ALWAYS_RETRY_ON_ERROR --> DLQ‑топик
    )
    @KafkaListener(
        topics = KafkaTopicConfig.ANALYTICS_EVENTS_TOPIC,
        groupId = "analytics-consumer-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeEvent(
        @Payload AnalyticsEventRequest event,
        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
        @Header(KafkaHeaders.OFFSET) long offset,
        Acknowledgment acknowledgment
    ) {
        try {
            log.info("Consuming event from Kafka: type={}, sessionId={}, partition={}, offset={}",
                event.eventType(), event.sessionId(), partition, offset);

            analyticsService.recordEvent(event);

            acknowledgment.acknowledge();

            log.debug("Event processed successfully: offset={}, partition={}", offset, partition);

        } catch (Exception ex) {
            log.error("Error processing event from Kafka: sessionId={}, eventType={}, offset={}, partition={}",
                event.sessionId(), event.eventType(), offset, partition, ex);

//            acknowledgment.acknowledge(); // TODO: !!! at least once
        }
    }
}
