package com.example.tpirates.store.model;

import com.example.tpirates.store.BusinessStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseStores {

    private String name;
    private String description;
    private int level;
    private BusinessStatus businessStatus;

    @Builder
    public ResponseStores(String name, String description, int level, BusinessStatus businessStatus) {
        this.name = name;
        this.description = description;
        this.level = level;
        this.businessStatus = businessStatus;
    }
}
