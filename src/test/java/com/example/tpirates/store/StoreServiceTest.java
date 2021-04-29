package com.example.tpirates.store;

import com.example.tpirates.exception.businessException.EntityNotFoundException;
import com.example.tpirates.store.model.RequestCreateStore;
import com.example.tpirates.store.model.RequestCreateStoreHolidays;
import com.example.tpirates.store.model.ResponseStores;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
public class StoreServiceTest {

    @Autowired
    StoreService storeService;
    @Autowired
    StoreRepository storeRepository;

    @BeforeEach
    void init() {
        List<Store> stores = StoreDummy.getStores();
        storeRepository.saveAll(stores);
    }

    @Test
    @DisplayName("점포 추가_성공")
    public void saveStore_success() throws Exception {
        //given : 추가할 점포 존재
        RequestCreateStore requestStore = RequestCreateStore.builder()
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

        //when : 점포 추가 요청
        Store savedStore = storeService.saveStore(requestStore);

        //then : 점포 정상 생성 확인
        Optional<Store> findStore = storeRepository.findById(savedStore.getId());
        assertAll(
                () -> assertThat(findStore.isPresent()).isTrue(),
                () -> assertThat(findStore.get().getName()).isEqualTo("인어수산"),
                () -> assertThat(findStore.get().getOwner()).isEqualTo("장인어"),
                () -> assertThat(findStore.get().getDescription()).isEqualTo("인천소래포구 종합어시장 갑각류센터 인어수산"),
                () -> assertThat(findStore.get().getLevel()).isEqualTo(2),
                () -> assertThat(findStore.get().getAddress()).isEqualTo("인청광역시 남동구 논현동 680-1 소래포구 종합어시장 1층 1호"),
                () -> assertThat(findStore.get().getPhone()).isEqualTo("010-1111-2222"),
                () -> assertThat(findStore.get().getBusinessTimes().size()).isEqualTo(5)
        );
    }

    @Test
    @DisplayName("점포 휴무일 추가_성공")
    public void saveStoreHoliday_success() throws Exception {
        //given : 휴무일을 등록할 점포, 휴무일 요청 존재
        Store store = Store.builder()
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
        storeRepository.save(store);

        RequestCreateStoreHolidays requestCreateStoreHolidays = RequestCreateStoreHolidays.builder()
                .id(store.getId())
                .holidays(new HashSet<>(Arrays.asList(
                        LocalDate.parse("2021-04-30", DateTimeFormatter.ISO_DATE),
                        LocalDate.parse("2021-05-01", DateTimeFormatter.ISO_DATE)
                ))).build();

        //when : 점포에 휴무일(2021-04-30, 2021-05-01) 추가 요청
        storeService.saveStoreHoliday(requestCreateStoreHolidays);

        //then : 점포에 휴무일(2021-04-30, 2021-05-01) 정상 추가 확인
        Set<LocalDate> holidays = store.getHolidays();
        holidays.forEach(holiday -> {
            assertThat(holiday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).isIn("2021-04-30","2021-05-01");
        });
    }

    @Test
    @DisplayName("점포 휴무일 추가_실패_존재하지 않는 점포")
    public void saveStoreHoliday_fail_entityNotFound() throws EntityNotFoundException {
        //given : 존재하지 않는 점포 id, 휴무일 요청 존재
        Long id = Long.MAX_VALUE;

        RequestCreateStoreHolidays requestCreateStoreHolidays = RequestCreateStoreHolidays.builder()
                .id(id)
                .holidays(new HashSet<>(Arrays.asList(
                        LocalDate.parse("2021-04-30", DateTimeFormatter.ISO_DATE),
                        LocalDate.parse("2021-05-01", DateTimeFormatter.ISO_DATE)
                ))).build();

        //when : 점포에 휴무일(2021-04-30, 2021-05-01) 추가 요청
        //then : 존재하지 않는 점포에 휴무일 추가 요청으로 인해 EntityNotFoundException 에러 발생 확인
        assertThrows(EntityNotFoundException.class, () -> {
            storeService.saveStoreHoliday(requestCreateStoreHolidays);
        });
    }

//    @Test
//    @DisplayName("점포 목록 조회_성공")
//    public void getStores_success()  {
//        //given : 인어수산, 해적수산 점포 2개 존재
//        //when : 점포 목록 전체 조회
//        List<ResponseStores> findStores = storeService.getStores();
//
//        //then : 존재하지 않는 점포에 휴무일 추가 요청으로 인해 EntityNotFoundException 에러 발생 확인
//
//    }

}
