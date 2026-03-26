package org.example.acs_v2.dto;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * DTO для регистрации пользователя
 */
@Data
public class UserRegistrationDto {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 4, max = 1000)
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    @Pattern(
            regexp = "^\\+375(\\(\\d{2}\\)\\d{3}-\\d{2}-\\d{2}|\\d{9})$",
            message = "Телефон должен быть в формате +375(XX)XXX-XX-XX или +375XXXXXXXXX"
    )
    private String numberPhone;

    /**
     * Привязка формы: поле в HTML называется `number_phone`.
     * Spring связывает по имени свойства, поэтому делаем bridge setter/getter.
     */
    public void setNumber_phone(String number_phone) {
        this.numberPhone = number_phone;
    }

    public String getNumber_phone() {
        return numberPhone;
    }
}
