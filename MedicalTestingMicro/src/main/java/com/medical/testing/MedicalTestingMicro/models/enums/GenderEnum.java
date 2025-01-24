package com.medical.testing.MedicalTestingMicro.models.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum GenderEnum {
    MALE("MALE"),
    FEMALE("FEMALE"),
    OTHER("OTHER");

    private final String value;

    GenderEnum(String value) {
        this.value = value;
    }

    public static GenderEnum findByValue(String value) {
        GenderEnum result = GenderEnum.MALE;

        for (GenderEnum anEnum : values()) {
            if (anEnum.getValue().equalsIgnoreCase(value)) {
                result = anEnum;
                break;
            }
        }

        return result;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
