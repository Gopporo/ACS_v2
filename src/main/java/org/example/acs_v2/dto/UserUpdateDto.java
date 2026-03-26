package org.example.acs_v2.dto;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * DTO для обновления данных пользователя
 */
@Data
public class UserUpdateDto {
    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    // Пароль можно не обновлять (пусто в форме) — поэтому без обязательных ограничений
    private String password;
    private String position;
    private Integer userAccessLvl;
    private Long departmentId;
}
