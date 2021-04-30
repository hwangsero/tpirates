package com.example.tpirates.store.model;

import com.example.tpirates.store.BusinessDay;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class ResponseStoreDetail {

    private Long id;
    private String name;
    private String description;
    private int level;
    private String address;
    private String phone;
    private List<BusinessDay> businessDays = new ArrayList<>();

    @Builder
    public ResponseStoreDetail(Long id, String name, String description, int level, String address, String phone, List<BusinessDay> businessDays) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.level = level;
        this.address = address;
        this.phone = phone;
        this.businessDays = businessDays;
    }
}
