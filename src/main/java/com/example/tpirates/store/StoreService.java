package com.example.tpirates.store;

import com.example.tpirates.exception.businessException.EntityNotFoundException;
import com.example.tpirates.store.model.RequestCreateStore;
import com.example.tpirates.store.model.RequestCreateStoreHolidays;
import com.example.tpirates.store.model.ResponseStores;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

//    public List<ResponseStores> getStores() {
//        List<Store> findStores = storeRepository.findAllByOrderByLevelAsc();
//
//
//    }
//
//    public BusinessStatus getBusinessStatus() {
//
//    }
}
