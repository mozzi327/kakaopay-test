package com.kakaopay.kakao.outbox.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.kakaopay.kakao.utils.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventBuilder implements OutBoxEventBuilder<OrderCreated> {

    private static final String EVENT_ACTION = "주문";

    @Override
    public OutBoxEvent createOutBoxEvent(OrderCreated domainEvent) {
        JsonNode jsonNode = ObjectMapperUtil.getMapper().convertValue(domainEvent, JsonNode.class);

        return new OutBoxEvent.OutBoxEventBuilder()
                .aggregateId((long) domainEvent.getItemNumber())
                .aggregateType(OrderCreated.class.getSimpleName())
                .eventType(domainEvent.getClass().getSimpleName())
                .payload(jsonNode.toString())
                .eventAction(EVENT_ACTION)
                .build();
    }
}
