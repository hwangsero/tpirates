package com.example.tpirates.store;

import com.example.tpirates.exception.businessException.EntityNotFoundException;
import com.example.tpirates.store.model.RequestCreateStoreHolidays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class StoreRepositoryTest {

    @Autowired
    StoreRepository storeRepository;

    @BeforeEach
    void init() {
        List<Store> stores = StoreDummy.getStores();
        storeRepository.saveAll(stores);
    }

    @Test
    @DisplayName("점포 상세 목록 조회_성공")
    public void findAllByOrderByLevelAsc_success() throws Exception {
        //given : 인어수산, 해적수산 점포 2개 존재
        //when : 점포들의 상세 정보를 등급(level) 오름차순으로 전체 조회
        List<Store> findStores = storeRepository.findAllByOrderByLevelAsc();

        //then : 등급(level) 오름차순으로 정상 조회 확인
        List<Store> stores = StoreDummy.getStores();
        stores.sort(new Comparator<Store>() {
            @Override
            public int compare(Store o1, Store o2) {
                return o1.getLevel()-o2.getLevel();
            }
        });
        assertAll(
                () -> assertThat(findStores.size()).isEqualTo(stores.size()),
                () -> assertThat(findStores.get(0).getLevel()).isEqualTo(stores.get(0).getLevel()),
                () -> assertThat(findStores.get(1).getLevel()).isEqualTo(stores.get(1).getLevel())
        );

    }
}
