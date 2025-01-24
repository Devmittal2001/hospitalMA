package com.medical.testing.MedicalTestingMicro.models.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RoleEnum {
    USER("USER"),
    ADMIN("ADMIN"),
    STAFF("STAFF");

    private final String value;

    RoleEnum(String value) {
        this.value = value;
    }

    public static RoleEnum findByValue(String value) {
        RoleEnum result = RoleEnum.USER;

        for (RoleEnum anEnum : values()) {
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
