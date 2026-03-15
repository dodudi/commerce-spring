package com.commerce.event;

import com.commerce.domain.Outbox;
import com.commerce.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publish() {
        List<Outbox> pendingList = outboxRepository.findByStatus(Outbox.OutboxStatus.PENDING);
        for (Outbox outbox : pendingList) {
            try {
                kafkaTemplate.send(outbox.getTopic(), outbox.getPayload());
                outbox.markPublished();
                log.info("Outbox 발행 성공: outboxId={}", outbox.getId());
            } catch (Exception e) {
                log.error("Outbox 발행 실패, 다음 스케줄에 재시도: outboxId={}", outbox.getId(), e);
            }
        }
    }
}
