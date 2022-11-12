package com.kakaopay.kakao.controller;

import com.kakaopay.kakao.dto.OrderProductDTO;
import com.kakaopay.kakao.dto.PayApprovalDTO;
import com.kakaopay.kakao.response.Message;
import com.kakaopay.kakao.service.CartService;
import com.kakaopay.kakao.service.KakaoPayservice;
import com.kakaopay.kakao.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.kakaopay.kakao.utils.OrderConstraint.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/order")
public class ReserveController {
    private final KakaoPayservice kakaoPayservice;
    private final OrderService orderService;
    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<Message> getOrderInfo(@PathVariable String userId) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(
                Message.builder()
                        .data(orderService.getOrderInfoList(userId))
                        .message(MEMBER_ORDER_LIST)
                        .build());
    }

    /**
     * 주문(예약하기) 메서드
     * @param productDTO product 정보에 대한 입력 DTO
     * @param userId
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<Message> orderAction(@RequestBody OrderProductDTO productDTO,
                                               @CookieValue(value = "userId") String userId,
                                               HttpServletRequest request) {
        String requestUrl = request.getRequestURL()
                .toString()
                .replace(request.getRequestURI(), "");
        String url = kakaoPayservice.getKakaoPayUrl(productDTO, userId, requestUrl);

        log.info("->>>>>>>>>>>>>> " + url);

        if (url == null) {
            getFailedPayMessage();
        }
        return ResponseEntity.ok().body(
                Message.builder()
                        .data(url)
                        .message(PAY_URI_MSG)
                        .build());
    }


    private ResponseEntity<Message> getFailedPayMessage() {
        return ResponseEntity.badRequest().body(
                Message.builder()
                        .message(FAILED_INFO_MESSAGE + "<br>" + INVALID_PARAMS)
                        .build()
        );
    }

    @GetMapping("/completed")
    public ResponseEntity<Message> paySuccessAction(@RequestParam("pg_token") String pgToken) {
        PayApprovalDTO payInfo = kakaoPayservice.getApprovedKaKaoPayInfo(pgToken);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(
                Message.builder()
                        .data(payInfo)
                        .message(INFO_URI_MSG)
                        .build()
        );
    }
}
