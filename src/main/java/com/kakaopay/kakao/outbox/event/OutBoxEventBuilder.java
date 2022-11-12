package com.kakaopay.kakao.outbox.event;

public interface OutBoxEventBuilder <T> {

    OutBoxEvent createOutBoxEvent(T domainEvent);
}
