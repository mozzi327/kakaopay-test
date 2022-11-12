package com.kakaopay.kakao.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 응답 body에 보낼 메시지 클래스<br>
 * 빌더 패턴 적용
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message <T> {
    private T data;
    private String message;
}
