package com.example.tpirates.store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StoreDummy {

    public static List<Store> getStores() {
        Store storeA = Store.builder()
                .name("인어수산")
                .owner("장인어")
                .description("인천소래포구 종합어시장 갑각류센터 인어수산")
                .level(2)
                .address("인청광역시 남동구 논현동 680-1 소래포구 종합어시장 1층 1호")
                .phone("010-1111-2222")
                .businessTimes(new ArrayList<>(Arrays.asList(
                        new BusinessTime(Day.Monday,"13:00","23:00"),
                        new BusinessTime(Day.Tuesday,"13:00","23:00"),
                        new BusinessTime(Day.Wednesday,"09:00","18:00"),
                        new BusinessTime(Day.Thursday,"09:00","23:00"),
                        new BusinessTime(Day.Friday,"09:00","23:00")
                ))).build();

        Store storeB = Store.builder()
                .name("해적수산")
                .owner("박해적")
                .description("노량진 시장 광어, 참돔 등 싱싱한 고퀄 활어 전문 횟집")
                .level(1)
                .address("서울 동작구 노량진동 13-8 노량진수산시장 활어 001")
                .phone("010-1234-1234")
                .businessTimes(new ArrayList<>(Arrays.asList(
                        new BusinessTime(Day.Monday,"09:00","24:00"),
                        new BusinessTime(Day.Tuesday,"09:00","24:00"),
                        new BusinessTime(Day.Wednesday,"09:00","24:00"),
                        new BusinessTime(Day.Thursday,"09:00","24:00"),
                        new BusinessTime(Day.Friday,"09:00","24:00")
                ))).build();
        List<Store> stores = new ArrayList<>(Arrays.asList(storeA,storeB));
        return stores;
    }
}
