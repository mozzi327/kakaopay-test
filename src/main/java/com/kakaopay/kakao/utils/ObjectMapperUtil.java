package com.kakaopay.kakao.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.format.DateTimeFormatter;

/**
 * Object Mapper Util 클래스 <br>
 * 세팅된 mapper 객체를 반환한다.
 */
public class ObjectMapperUtil {

    private ObjectMapperUtil() {
    }

    public static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(new LocalDateTimeSerializer(DateTimeFormatter
                .ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")));
        mapper.registerModule(javaTimeModule);
//        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        return mapper;
    }
}
