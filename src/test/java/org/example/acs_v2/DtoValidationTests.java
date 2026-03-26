package org.example.acs_v2;

import org.example.acs_v2.dto.UserRegistrationDto;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DtoValidationTests {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void userRegistrationDto_invalidFields_hasViolations() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setEmail("");
        dto.setPassword("123");
        dto.setName("");
        dto.setNumber_phone("bad-phone");

        Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")),
                "Expected violation for email"
        );
        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")),
                "Expected violation for password"
        );
        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")),
                "Expected violation for name"
        );
        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("numberPhone")),
                "Expected violation for numberPhone"
        );
    }
}

