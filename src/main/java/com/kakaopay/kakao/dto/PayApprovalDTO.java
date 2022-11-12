package com.kakaopay.kakao.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PayApprovalDTO {
    private String aid;
    private String tid;
    private String cid;
    private String sid;
    private String partnerOrderId;
    private String partnerUserId;
    private String paymentMethodType;
    private Amount amount;
    private CardInfo cardInfo;
    private String itemName;
    private String itemCode;
    private String payload;
    private Integer quantity;
    private Integer taxFreeAmount;
    private Integer vatAmount;
    private Date createdAt;
    private Date approvedAt;
    private String orderStatus;

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
