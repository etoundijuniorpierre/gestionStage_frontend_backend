package com.internship.management.customAnnotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MatriculationValidator.class)
public @interface ValidMatriculation {
    String message() default "Enterprise does not exist in trade register";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
