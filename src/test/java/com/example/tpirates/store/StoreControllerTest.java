package com.example.tpirates.store;

import com.example.tpirates.exception.businessException.BusinessException;
import com.example.tpirates.exception.businessException.EntityNotFoundException;
import com.example.tpirates.exception.businessException.ValidationException;
import com.example.tpirates.infra.MockMvcTest;
import com.example.tpirates.store.model.RequestCreateStore;
import com.example.tpirates.store.model.RequestCreateStoreHolidays;
import com.example.tpirates.store.model.ResponseStores;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
public class StoreControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    StoreRepository storeRepository;

    private Class<? extends Exception> getApiResultExceptionClass(MvcResult result) {
        return Objects.requireNonNull(result.getResolvedException()).getClass();
    }

    @BeforeEach
    void init() {
        List<Store> stores = StoreDummy.getStores();
        storeRepository.saveAll(stores);
    }

    @Test
    @DisplayName("점포 추가_성공")
    public void createStore_success() throws Exception {
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

        //when : post, /api/store 으로 요청 시 점포 추가
        MvcResult mvcResult = mockMvc.perform(post("/api/store")
                .content(objectMapper.writeValueAsString(requestStore))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then : 점포 정상 추가 확인
        String strResult = mvcResult.getResponse().getContentAsString();
        Store createdStore = objectMapper.readValue(strResult, Store.class);
        assertAll(
                () -> assertThat(createdStore.getName()).isEqualTo("인어수산"),
                () -> assertThat(createdStore.getOwner()).isEqualTo("장인어"),
                () -> assertThat(createdStore.getDescription()).isEqualTo("인천소래포구 종합어시장 갑각류센터 인어수산"),
                () -> assertThat(createdStore.getLevel()).isEqualTo(2),
                () -> assertThat(createdStore.getAddress()).isEqualTo("인청광역시 남동구 논현동 680-1 소래포구 종합어시장 1층 1호"),
                () -> assertThat(createdStore.getPhone()).isEqualTo("010-1111-2222"),
                () -> assertThat(createdStore.getBusinessTimes().size()).isEqualTo(5)
        );
    }

    @Test
    @DisplayName("점포 추가_실패_유효성 위반_전화번호")
    public void createStore_fail_validation_phone() throws Exception {
        //given : 잘못된 입력 값의 전화번호를 포함한 추가할 점포 존재
        RequestCreateStore requestStore = RequestCreateStore.builder()
                .name("인어수산")
                .owner("장인어")
                .description("인천소래포구 종합어시장 갑각류센터 인어수산")
                .level(2)
                .address("인청광역시 남동구 논현동 680-1 소래포구 종합어시장 1층 1호")
                .phone("01011112222")
                .businessTimes(new ArrayList<>(Arrays.asList(
                        new BusinessTime(Day.Monday,"13:00","23:00"),
                        new BusinessTime(Day.Tuesday,"13:00","23:00"),
                        new BusinessTime(Day.Wednesday,"09:00","18:00"),
                        new BusinessTime(Day.Thursday,"09:00","23:00"),
                        new BusinessTime(Day.Friday,"09:00","23:00")
                ))).build();

        //when : post, /api/store 으로 요청 시 점포 추가 요청
        //then : 전화번호(phone)의 잘못된 입력값 형태로 인한 Validation 위반으로 4xx 에러, MethodArgumentNotValidException 발생 확인
        MvcResult mvcResult = mockMvc.perform(post("/api/store")
                .content(objectMapper.writeValueAsString(requestStore))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result ->
                        assertThat(getApiResultExceptionClass(result)).isEqualTo(MethodArgumentNotValidException.class))
                .andReturn();

    }

    @Test
    @DisplayName("점포 추가_실패_유효성 위반_영업시간")
    public void createStore_fail_validation_businessTime() throws Exception {
        //given : 잘못된 입력 값의 영업시간 존재(영업 시작 시간과 종료시간은 같을 수 없다)를 포함한 추가할 점포 존재
        RequestCreateStore requestStore = RequestCreateStore.builder()
                .name("인어수산")
                .owner("장인어")
                .description("인천소래포구 종합어시장 갑각류센터 인어수산")
                .level(2)
                .address("인청광역시 남동구 논현동 680-1 소래포구 종합어시장 1층 1호")
                .phone("010-1111-2222")
                .businessTimes(new ArrayList<>(Arrays.asList(
                        new BusinessTime(Day.Monday,"13:00","13:00"),
                        new BusinessTime(Day.Tuesday,"13:00","23:00"),
                        new BusinessTime(Day.Wednesday,"09:00","18:00"),
                        new BusinessTime(Day.Thursday,"09:00","23:00"),
                        new BusinessTime(Day.Friday,"09:00","23:00")
                ))).build();

        //when : post, /api/store 으로 요청 시 점포 추가 요청
        //then : 영업시간(businessTime)의 잘못된 입력값 형태로 인한 Validation 위반으로 4xx 에러, ValidationException 발생 확인
        MvcResult mvcResult = mockMvc.perform(post("/api/store")
                .content(objectMapper.writeValueAsString(requestStore))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result ->
                        assertThat(getApiResultExceptionClass(result)).isEqualTo(ValidationException.class))
                .andReturn();

    }

    @Test
    @DisplayName("점포 휴무일 등록_성공")
    public void createStoreHoliday_success() throws Exception {
        //given : 휴무일을 등록할 점포 존재
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

        //when : post, /api/store/holiday 으로 요청 시 특정 점포에 휴무일 추가(2021-04-30, 2021-05-01)
        mockMvc.perform(post("/api/store/holiday")
                .content("{\"id\":\"" + store.getId() + "\", \"holidays\":[\"2021-04-30\",\"2021-05-01\"]}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then : 점포에 휴무일(2021-04-30, 2021-05-01) 정상 추가 확인
        Set<LocalDate> holidays = store.getHolidays();
        holidays.forEach(holiday -> {
            assertThat(holiday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).isIn("2021-04-30","2021-05-01");
        });
    }

    @Test
    @DisplayName("점포 휴무일 등록_실패_존재하지 않는 점포")
    public void createStoreHoliday_fail_entityNotFound() throws Exception {
        //given : 존재하지 않는 점포 id
        Long id = Long.MAX_VALUE;
        //when : post, /api/store/{id}/holiday 으로 요청 시 특정 점포에 휴무일 추가
        //then : 존재하지 않는 점포 아이디에 대한 휴무일 추가 시도로 인해 4xx 에러, EntityNotFoundException 발생 확인
        mockMvc.perform(post("/api/store/holiday")
                .content("{\"id\":\"" + id + "\", \"holidays\":[\"2021-04-30\",\"2021-05-01\"]}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertThat(getApiResultExceptionClass(result)).isEqualTo(EntityNotFoundException.class))
                .andReturn();
    }

//    @Test
//    @DisplayName("점포 목록 조회_성공")
//    public void getStores_success() throws Exception {
//        //given : 인어수산, 해적수산 점포 2개 존재
//        //when : get, /api/stores 으로 요청 시 점포 목록 전체 조회
//        MvcResult mvcResult = mockMvc.perform(get("/api/stores"))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        //then : 인어수산, 해적수산 점포 2개 정상 조회 확인
//        String strResult = mvcResult.getResponse().getContentAsString();
//        List<ResponseStores> responseStores = objectMapper.readValue(strResult, new TypeReference<List<ResponseStores>>() {});
//
//    }


}
