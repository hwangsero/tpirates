package com.example.tpirates.store;

import com.example.tpirates.exception.businessException.EntityNotFoundException;
import com.example.tpirates.store.model.RequestCreateStore;
import com.example.tpirates.store.model.RequestCreateStoreHolidays;
import com.example.tpirates.store.model.ResponseStore;
import com.example.tpirates.store.model.ResponseStoreDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;

    @Transactional
    public Store saveStore(RequestCreateStore requestCreateStore) {
        Store createdStore = Store.builder()
                .name(requestCreateStore.getName())
                .owner(requestCreateStore.getOwner())
                .description(requestCreateStore.getDescription())
                .level(requestCreateStore.getLevel())
                .address(requestCreateStore.getAddress())
                .phone(requestCreateStore.getPhone())
                .businessTimes(requestCreateStore.getBusinessTimes())
                .build();
        return storeRepository.save(createdStore);
    }

    @Transactional
    public void saveStoreHoliday(RequestCreateStoreHolidays requestCreateStoreHolidays) {
        Store findStore = storeRepository.findById(requestCreateStoreHolidays.getId()).orElseThrow(
                () -> new EntityNotFoundException("store"));
        findStore.addHolidays(requestCreateStoreHolidays.getHolidays());
        storeRepository.save(findStore);
    }

    public List<ResponseStore> getStores() {
        List<Store> findStores = storeRepository.findAllByOrderByLevelAsc();
        List<ResponseStore> responseStores = new ArrayList<>();
        findStores.forEach(store -> {
            ResponseStore responseStore = ResponseStore.builder()
                    .name(store.getName())
                    .description(store.getDescription())
                    .level(store.getLevel())
                    .businessStatus(getBusinessStatus(LocalDateTime.now(), store.getBusinessTimes(), store.getHolidays())).build();
            responseStores.add(responseStore);
        });
        return responseStores;
    }

    public ResponseStoreDetail getStoreDetail(Long id) {
        Store findStore = storeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("store"));
        List<BusinessDay> businessDays = getThreeWorkingDay(LocalDateTime.now(),findStore.getBusinessTimes(), findStore.getHolidays());

        return ResponseStoreDetail.builder()
                .id(findStore.getId())
                .name(findStore.getName())
                .description(findStore.getDescription())
                .level(findStore.getLevel())
                .address(findStore.getAddress())
                .phone(findStore.getPhone())
                .businessDays(businessDays) .build();
    }

    public List<BusinessDay> getThreeWorkingDay(LocalDateTime localDateTime, List<BusinessTime> businessTimes, Set<LocalDate> holidays) {
        List<BusinessDay> businessDays = new ArrayList<>();
        int workingDayCount = 3;
        while(workingDayCount > 0) {
            int currentDayOfWeek = localDateTime.getDayOfWeek().getValue();

            BusinessStatus businessStatus = getBusinessStatus(
                    LocalDateTime.of(localDateTime.getYear(),localDateTime.getMonth(),localDateTime.getDayOfMonth(),localDateTime.getHour(),localDateTime.getMinute())
                    , businessTimes, holidays);

            BusinessTime currentBusinessTime = businessTimes.stream()
                    .filter(businessTime -> businessTime.getDay().day == currentDayOfWeek)
                    .findAny().orElse(null);
            if(currentBusinessTime != null) {
                businessDays.add(BusinessDay.builder()
                        .day(currentBusinessTime.getDay())
                        .open(currentBusinessTime.getOpen())
                        .close(currentBusinessTime.getClose())
                        .statue(businessStatus).build());
                workingDayCount--;
            }
            localDateTime = localDateTime.plusDays(1);
        }
        return businessDays;
    }

    public BusinessStatus getBusinessStatus(LocalDateTime localDateTime, List<BusinessTime> businessTimes, Set<LocalDate> holidays) {
        LocalDate currentDate = LocalDate.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth());
        LocalTime currentTime = LocalTime.of(localDateTime.getHour(), localDateTime.getMinute());
        // ?????? ?????? ??????(???-1...???-7)
        int currentDayOfWeek = localDateTime.getDayOfWeek().getValue();
        // ?????? ????????? ???????????? BusinessTime ??????
        BusinessTime currentBusinessTime = businessTimes.stream()
                .filter(businessTime -> businessTime.getDay().day == currentDayOfWeek)
                .findAny().orElse(null);
        // ?????? ???????????? null(????????? or ?????????) ????????? ????????? ????????? ???????????? return HOLIDAY
        if(currentBusinessTime == null || (holidays != null && holidays.contains(currentDate))) return BusinessStatus.HOLIDAY;

        Long open = currentBusinessTime.getOpen().getLong(ChronoField.MINUTE_OF_DAY);
        Long close = currentBusinessTime.getClose().getLong(ChronoField.MINUTE_OF_DAY);
        // ?????? open time <= ???????????? <= ?????? close time ??? ?????? return OPEN
        // if) ??????????????? 12:00 ~ 02:00 ??? ?????? ??????(??????????????? ???????????? ?????????)
        if(open > close) {
            if(currentTime.getLong(ChronoField.MINUTE_OF_DAY) <= close || currentTime.getLong(ChronoField.MINUTE_OF_DAY) >= open)
                return BusinessStatus.OPEN;
        }
        // else) ??????????????? 12:00 ~ 23:00 ??? ?????? ??????(??????????????? ???????????? ???????????? ??????)
        else {
            if (currentTime.getLong(ChronoField.MINUTE_OF_DAY) >= open
                    && currentTime.getLong(ChronoField.MINUTE_OF_DAY) <= close)
                return BusinessStatus.OPEN;
        }
        // ??? ?????? ???????????? < ?????? open time ????????? ???????????? > ?????? close time ??? ?????? return CLOSE
        return BusinessStatus.CLOSE;
    }
}
