package com.medical.testing.MedicalTestingMicro.models.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Objects;

@Converter(autoApply = true)
public class GenderEnumConverter implements AttributeConverter<GenderEnum, String> {
    
    @Override
    public String convertToDatabaseColumn(GenderEnum genderEnum) {
        return Objects.requireNonNullElse(genderEnum, GenderEnum.MALE).getValue();
    }

    @Override
    public GenderEnum convertToEntityAttribute(String value) {
        return GenderEnum.findByValue(value);
    }

}
