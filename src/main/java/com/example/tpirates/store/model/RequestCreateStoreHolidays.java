package com.example.tpirates.store.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
public class RequestCreateStoreHolidays {

    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Set<LocalDate> holidays = new HashSet<>();

    @Builder
    public RequestCreateStoreHolidays(Long id, Set<LocalDate> holidays) {
        this.id = id;
        this.holidays = holidays;
    }
}
