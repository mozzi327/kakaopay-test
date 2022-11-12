package com.kakaopay.kakao.outbox.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreated {
    private int quantity;
    private int itemNumber;
    private String itemName;
    private int totalAmount;
}
