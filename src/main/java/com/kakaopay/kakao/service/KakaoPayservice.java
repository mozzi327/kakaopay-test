package com.kakaopay.kakao.service;

import com.kakaopay.kakao.dto.OrderProductDTO;
import com.kakaopay.kakao.dto.PayApprovalDTO;
import com.kakaopay.kakao.dto.PayReadyDTO;
import com.kakaopay.kakao.outbox.event.OrderCreated;
import com.kakaopay.kakao.outbox.event.OutBoxEventBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static com.kakaopay.kakao.utils.PayConstraint.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoPayservice {

    @Value("${kakao.admin.key}")
    private String adminKey;
    @Value("${kakao.host}")
    private String host;
    @Value("${kakao.uri.approval}")
    private String approvalUri;
    @Value("${kakao.uri.cancel}")
    private String cancelUri;
    @Value("${kakao.uri.fail}")
    private String failUri;
    @Value("${kakao.pay.ready}")
    private String kakaoPayReady;
    @Value("${kakao.pay.approve}")
    private String kakaoPayApprove;
    @Value("${kakao.pay.cid}")
    private String testCid;
    @Value("${kakao.pay.taxfree}")
    private Integer taxFreeAmount;
    @Value("${kakao.pay.cancel}")
    private String kakaoPayCancel;
    @Value("${kakao.pay.order}")
    private String kakaoPayOrder;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final OutBoxEventBuilder<OrderCreated> outBoxEventOrderBuilder;

    private PayReadyDTO payReadyDTO;
    private RestTemplate restTemplate;
    private String orderId;
    private String userId;
    private String itemName;
    private Integer totalAmount;
//    private UserVO user;
    private boolean exceptionFlag = true;

    public String getKakaoPayUrl(OrderProductDTO productDTO,
                                 String userIds,
                                 String requestUrl) {

        applicationEventPublisher.publishEvent(
                outBoxEventOrderBuilder.createOutBoxEvent(OrderCreated.builder()
                        .itemNumber(productDTO.getItemNumber())
                        .quantity(productDTO.getQuantity())
                        .itemName(productDTO.getItemName())
                        .totalAmount(productDTO.getTotalAmount())
                        .build())
        );

        HttpHeaders headers = new HttpHeaders();
        setHeaders(headers);

        userId = String.valueOf(userIds); // tmp
        itemName = productDTO.getItemName();
        orderId = userId + "/" + itemName;
        totalAmount = productDTO.getTotalAmount();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        setParams(params, requestUrl);
        params.add(PARTNER_ORDER_ID, orderId);
        params.add(PARTNER_USER_ID, userId);
        params.add(ITEM_NAME, itemName);
        params.add(QUANTITY, String.valueOf(productDTO.getQuantity()));
        params.add(TOTAL_AMOUNT, String.valueOf(totalAmount));
        params.add(TAX_FREE_AMOUNT, String.valueOf(taxFreeAmount));

        String url = getPayUrl(headers, params);

        return url;
    }



    private String getPayUrl(HttpHeaders headers, MultiValueMap<String, String> params) {
        HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<>(params, headers);
        try {
            payReadyDTO = restTemplate.postForObject(host + kakaoPayReady,
                    body, PayReadyDTO.class);

            return payReadyDTO != null ? payReadyDTO.getNextRedirectPcUrl() : null;
        } catch (RestClientException e) {
            log.info(e.getMessage());
        }
        return null;
    }


    private void setHeaders(HttpHeaders headers) {
        restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        headers.add("Authorization", "KakaoAK " + adminKey);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
    }


    /**
     * 성공, 취소, 실패에 대한 리다이렉트 주를 세팅하는 메서드
     * @param params 빈 MultiValueMap
     * @param requestUrl Localhost 주소
     * @author mozzi327
     */
    private void setParams(MultiValueMap<String, String> params, String requestUrl) {
        params.add(CID, testCid);
        params.add(APPROVAL_URL, requestUrl + approvalUri);
        params.add(CANCEL_URL, requestUrl + cancelUri);
        params.add(FAIL_URL, requestUrl + failUri);
    }

    public PayApprovalDTO getApprovedKaKaoPayInfo(String pgToken) {
        HttpHeaders headers = new HttpHeaders();
        setHeaders(headers);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(CID, testCid);
        params.add(TID, payReadyDTO.getTid());
        params.add(PARTNER_ORDER_ID, orderId);
        params.add(PARTNER_USER_ID, userId);
        params.add(PG_TOKEN, pgToken);
        params.add(TOTAL_AMOUNT, String.valueOf(totalAmount));

        HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<>(params, headers);

        PayApprovalDTO approvalDTO = restTemplate.postForObject(host + kakaoPayApprove, body, PayApprovalDTO.class);

        if (approvalDTO == null) {
            return null;
        }

        approvalDTO.setOrderStatus(ORDER_APPROVED);

        try {
            return approvalDTO;
        } catch (RestClientException e) {
            log.error(e.getMessage());
        }

        return null;
    }
}
