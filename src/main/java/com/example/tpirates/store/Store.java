package com.example.tpirates.store;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String owner;
    private String description;
    private int level;
    private String address;
    private String phone;

    @ElementCollection
    @CollectionTable(name = "HOLIDAY",joinColumns = @JoinColumn(name="STORE_ID"))
    @Column(name = "HOLIDAY")
    private Set<LocalDate> holidays = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "BUSINESS_TIME",joinColumns = @JoinColumn(name="STORE_ID"))
    private List<BusinessTime> businessTimes = new ArrayList<>();

    @Builder
    public Store(Long id, String name, String owner, String description, int level, String address, String phone, Set<LocalDate> holidays, List<BusinessTime> businessTimes) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.level = level;
        this.address = address;
        this.phone = phone;
        this.holidays = holidays;
        this.businessTimes = businessTimes;
    }

    public void addHolidays(Set<LocalDate> holidays) {
        if(this.holidays == null) this.holidays = new HashSet<>();
        holidays.forEach(it -> {
            this.holidays.add(it);
        });
    }
}
