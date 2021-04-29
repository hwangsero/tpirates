package com.example.tpirates.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BusinessTime {

    @Enumerated(EnumType.STRING)
    private Day day;
    private String open;
    private String close;
}
