package com.kakaopay.kakao.outbox.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutBoxEvent {

    private Long aggregateId;

    private String aggregateType;

    private String eventType;

    private String payload;

    private String eventAction;

    private List<OrderCreated> cartList;
}
