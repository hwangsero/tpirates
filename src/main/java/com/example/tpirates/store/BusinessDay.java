package com.example.tpirates.store;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalTime;

@Embeddable
@NoArgsConstructor
@Getter
public class BusinessDay {

    @Enumerated(EnumType.STRING)
    private Day day;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime open;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime close;
    private BusinessStatus statue;

    @Builder
    public BusinessDay(Day day, LocalTime open, LocalTime close, BusinessStatus statue) {
        this.day = day;
        this.open = open;
        this.close = close;
        this.statue = statue;
    }
}
