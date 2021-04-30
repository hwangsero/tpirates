package com.example.tpirates.store;

import com.example.tpirates.exception.businessException.EntityNotFoundException;
import com.example.tpirates.store.model.RequestCreateStore;
import com.example.tpirates.store.model.RequestCreateStoreHolidays;
import com.example.tpirates.store.model.ResponseStore;
import com.example.tpirates.store.model.ResponseStoreDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
                        new BusinessTime(Day.Monday, LocalTime.of(13,00),LocalTime.of(23,00)),
                        new BusinessTime(Day.Tuesday,LocalTime.of(13,00),LocalTime.of(23,00)),
                        new BusinessTime(Day.Wednesday,LocalTime.of(9,00),LocalTime.of(18,00)),
                        new BusinessTime(Day.Thursday,LocalTime.of(9,00),LocalTime.of(23,00)),
                        new BusinessTime(Day.Friday,LocalTime.of(9,00),LocalTime.of(23,00))
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
                        new BusinessTime(Day.Monday, LocalTime.of(13,00),LocalTime.of(23,00)),
                        new BusinessTime(Day.Tuesday,LocalTime.of(13,00),LocalTime.of(23,00)),
                        new BusinessTime(Day.Wednesday,LocalTime.of(9,00),LocalTime.of(18,00)),
                        new BusinessTime(Day.Thursday,LocalTime.of(9,00),LocalTime.of(23,00)),
                        new BusinessTime(Day.Friday,LocalTime.of(9,00),LocalTime.of(23,00))
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

    @Test
    @DisplayName("점포 목록 조회_성공")
    public void getStores_success()  {
        //given : 인어수산, 해적수산 점포 2개 존재
        List<Store> stores = StoreDummy.getStores();
        stores.sort(new Comparator<Store>() {
            @Override
            public int compare(Store o1, Store o2) {
                return o1.getLevel()-o2.getLevel();
            }
        });

        //when : 점포 목록 전체 조회
        List<ResponseStore> findStores = storeService.getStores();

        //then : 인어수산, 해적수산 정상 조회 확인
        assertThat(stores.size()).isEqualTo(findStores.size());
        for(int i = 0; i < findStores.size(); i++) {
            assertThat(stores.get(i).getName()).isEqualTo(findStores.get(i).getName());
            assertThat(stores.get(i).getDescription()).isEqualTo(findStores.get(i).getDescription());
            assertThat(stores.get(i).getLevel()).isEqualTo(findStores.get(i).getLevel());
            assertThat(storeService.getBusinessStatus(LocalDateTime.now(), stores.get(i).getBusinessTimes(),stores.get(i).getHolidays()))
                    .isEqualTo(findStores.get(0).getBusinessStatus());
        }
    }

    @Test
    @DisplayName("점포 영업 상태 추출_종료시간이 다음날로 넘어가지 않을 경우")
    public void getBusinessStatus()  {
        //given : 영업시간이 09:00 ~ 23:00 인 점포 존재
        LocalDate now = LocalDate.now();
        List<BusinessTime> businessTimes = new ArrayList<>(Arrays.asList(
                new BusinessTime(Day.intToDay(now.getDayOfWeek().getValue()), LocalTime.of(9, 0), LocalTime.of(23, 0))));
        LocalDateTime case1 = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 8, 0); // 08:00
        LocalDateTime case2 = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 9, 0); // 09:00
        LocalDateTime case3 = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 10, 0); // 10:00
        LocalDateTime case4 = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 1, 0); // 01:00

        //when : 점포의 영업 상태 추출
        BusinessStatus result1 = storeService.getBusinessStatus(case1, businessTimes, new HashSet<>());
        BusinessStatus result2 = storeService.getBusinessStatus(case2, businessTimes, new HashSet<>());
        BusinessStatus result3 = storeService.getBusinessStatus(case3, businessTimes, new HashSet<>());
        BusinessStatus result4 = storeService.getBusinessStatus(case4, businessTimes, new HashSet<>());

        //then : 점포의 영업 상태 추출(테스트 일자가 토요일 or 일요일(휴일)이 아닐 경우에만 정상 테스트
        BusinessTime currentBusinessTime = businessTimes.stream()
                .filter(businessTime -> businessTime.getDay().day == now.getDayOfWeek().getValue())
                .findAny().orElse(null);
        if(currentBusinessTime != null) {
            assertAll(
                    () -> assertThat(result1).isEqualTo(BusinessStatus.CLOSE),
                    () -> assertThat(result2).isEqualTo(BusinessStatus.OPEN),
                    () -> assertThat(result3).isEqualTo(BusinessStatus.OPEN),
                    () -> assertThat(result4).isEqualTo(BusinessStatus.CLOSE)
            );
        }
    }


    @Test
    @DisplayName("점포 영업 상태 추출_종료시간이 다음날로 넘어가는 경우")
    public void getBusinessStatus_CloseOverDay()  {
        //given : 영업시간이 09:00 ~ 02:00 인 점포 존재
        LocalDate now = LocalDate.now();
        List<BusinessTime> businessTimes = new ArrayList<>(Arrays.asList(
                new BusinessTime(Day.intToDay(now.getDayOfWeek().getValue()), LocalTime.of(9, 0), LocalTime.of(2, 0))));
        LocalDateTime case1 = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 8, 0); // 08:00
        LocalDateTime case2 = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 9, 0); // 09:00
        LocalDateTime case3 = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 10, 0); // 10:00
        LocalDateTime case4 = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 1, 0); // 01:00
        LocalDateTime case5 = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 3, 0); // 03:00

        //when : 점포의 영업 상태 추출
        BusinessStatus result1 = storeService.getBusinessStatus(case1, businessTimes, new HashSet<>());
        BusinessStatus result2 = storeService.getBusinessStatus(case2, businessTimes, new HashSet<>());
        BusinessStatus result3 = storeService.getBusinessStatus(case3, businessTimes, new HashSet<>());
        BusinessStatus result4 = storeService.getBusinessStatus(case4, businessTimes, new HashSet<>());
        BusinessStatus result5 = storeService.getBusinessStatus(case5, businessTimes, new HashSet<>());

        //then : 점포의 영업 상태 추출(테스트 일자가 토요일 or 일요일(휴일)이 아닐 경우에만 정상 테스트
        BusinessTime currentBusinessTime = businessTimes.stream()
                .filter(businessTime -> businessTime.getDay().day == now.getDayOfWeek().getValue())
                .findAny().orElse(null);
        if(currentBusinessTime != null) {
            assertAll(
                    () -> assertThat(result1).isEqualTo(BusinessStatus.CLOSE),
                    () -> assertThat(result2).isEqualTo(BusinessStatus.OPEN),
                    () -> assertThat(result3).isEqualTo(BusinessStatus.OPEN),
                    () -> assertThat(result4).isEqualTo(BusinessStatus.OPEN),
                    () -> assertThat(result5).isEqualTo(BusinessStatus.CLOSE)
            );
        }
    }

    @Test
    @DisplayName("점포 상세 조회_성공")
    public void getStoreDetail_success()  {
        //given : 조회할 점포 존재
        Store store = Store.builder()
                .name("인어수산")
                .owner("장인어")
                .description("인천소래포구 종합어시장 갑각류센터 인어수산")
                .level(2)
                .address("인청광역시 남동구 논현동 680-1 소래포구 종합어시장 1층 1호")
                .phone("010-1111-2222")
                .businessTimes(new ArrayList<>(Arrays.asList(
                        new BusinessTime(Day.Monday, LocalTime.of(13,00),LocalTime.of(23,00)),
                        new BusinessTime(Day.Tuesday,LocalTime.of(13,00),LocalTime.of(23,00)),
                        new BusinessTime(Day.Wednesday,LocalTime.of(9,00),LocalTime.of(18,00)),
                        new BusinessTime(Day.Thursday,LocalTime.of(9,00),LocalTime.of(23,00)),
                        new BusinessTime(Day.Friday,LocalTime.of(9,00),LocalTime.of(23,00))
                ))).build();
        storeRepository.save(store);

        //when : 점포 상세 조회
        ResponseStoreDetail storeDetail = storeService.getStoreDetail(store.getId());

        //then : 점포 상세 정상 조회 확인
        assertAll(
                () -> assertThat(store.getId()).isEqualTo(storeDetail.getId()),
                () -> assertThat(store.getName()).isEqualTo(storeDetail.getName()),
                () -> assertThat(store.getDescription()).isEqualTo(storeDetail.getDescription()),
                () -> assertThat(store.getLevel()).isEqualTo(storeDetail.getLevel()),
                () -> assertThat(store.getAddress()).isEqualTo(storeDetail.getAddress()),
                () -> assertThat(store.getPhone()).isEqualTo(storeDetail.getPhone())
        );
    }

    @Test
    @DisplayName("점포 영업일 3일치 정보 조회")
    public void getThreeWorkingDay()  {
        //given : 점포의 영업정보, 테스트 일자(2021-04-30 금요일 10:00) 존재
        LocalDateTime localDateTime = LocalDateTime.of(2021, 04, 30, 10, 0);

        List<BusinessTime> businessTimes = new ArrayList<>(Arrays.asList(
                new BusinessTime(Day.Monday, LocalTime.of(13, 00), LocalTime.of(23, 00)),
                new BusinessTime(Day.Tuesday, LocalTime.of(13, 00), LocalTime.of(23, 00)),
                new BusinessTime(Day.Wednesday, LocalTime.of(9, 00), LocalTime.of(18, 00)),
                new BusinessTime(Day.Thursday, LocalTime.of(9, 00), LocalTime.of(23, 00)),
                new BusinessTime(Day.Friday, LocalTime.of(9, 00), LocalTime.of(23, 00))
        ));

        //when : 점포의 영업일 3일치 정보 조회
        List<BusinessDay> threeWorkingDay = storeService.getThreeWorkingDay(localDateTime, businessTimes, new HashSet<>());

        //then : 점포의 영업일 3일치 정보 정상 조회 확인
        assertAll(
                () -> assertThat(threeWorkingDay.get(0).getDay()).isEqualTo(Day.Friday),
                () -> assertThat(threeWorkingDay.get(0).getStatue()).isEqualTo(BusinessStatus.OPEN),
                () -> assertThat(threeWorkingDay.get(1).getDay()).isEqualTo(Day.Monday),
                () -> assertThat(threeWorkingDay.get(1).getStatue()).isEqualTo(BusinessStatus.CLOSE),
                () -> assertThat(threeWorkingDay.get(2).getDay()).isEqualTo(Day.Tuesday),
                () -> assertThat(threeWorkingDay.get(2).getStatue()).isEqualTo(BusinessStatus.CLOSE)
        );
    }

}
