package com.medical.testing.MedicalTestingMicro.models.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Objects;

@Converter(autoApply = true)
public class RoleEnumConverter implements AttributeConverter<RoleEnum, String> {

    @Override
    public String convertToDatabaseColumn(RoleEnum roleEnum) {
        return Objects.requireNonNullElse(roleEnum, RoleEnum.USER).getValue();
    }

    @Override
    public RoleEnum convertToEntityAttribute(String value) {
        return RoleEnum.findByValue(value);
    }

}
