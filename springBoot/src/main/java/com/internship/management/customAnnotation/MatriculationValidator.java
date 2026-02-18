package com.internship.management.customAnnotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class MatriculationValidator implements ConstraintValidator<ValidMatriculation, String> {


    private final List<String> tradeRegister = List.of(
            "RC/DLA/2023/A/04567",
            "RC/DLA/2022/B/12345",
            "RC/DLA/2021/C/99999",
            "RC/YAO/2023/B/01234",
            "RC/DLA/2022/A/07891",
            "RC/YAO/2024/B/03456",
            "RC/DLA/2023/B/06789",
            "RC/YAO/2022/A/09876",
            "RC/DLA/2024/B/05555",
            "RC/YAO/2023/A/02345"
    );

    @Override
    public boolean isValid(String matriculation, ConstraintValidatorContext context) {
        return matriculation != null && tradeRegister.contains(matriculation);
    }
}
