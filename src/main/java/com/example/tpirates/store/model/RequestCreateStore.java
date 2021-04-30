package com.example.tpirates.store.model;

import com.example.tpirates.store.BusinessTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@NoArgsConstructor
public class RequestCreateStore {

    @NotEmpty
    private String name;
    @NotEmpty
    private String owner;
    private String description;
    @NotEmpty
    private int level;
    private String address;
    @Pattern(regexp="^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", message="전화번호 입력값이 올바르지 않습니다.")
    private String phone;
    private List<BusinessTime> businessTimes;

    @Builder
    public RequestCreateStore(String name, String owner, String description, int level, String address, String phone, List<BusinessTime> businessTimes) {
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.level = level;
        this.address = address;
        this.phone = phone;
        this.businessTimes = businessTimes;
    }
}
