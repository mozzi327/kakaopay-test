package com.kakaopay.kakao.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {


    @Transactional(readOnly = true)
    public Object getOrderInfoList(String userId) {
        return new ArrayList<>(List.of("참깨빵", "단팥빵"));
    }
}
