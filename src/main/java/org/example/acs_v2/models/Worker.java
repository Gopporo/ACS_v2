package org.example.acs_v2.models;

import lombok.Data;
import org.example.acs_v2.models.enums.AccessLevel;
import org.example.acs_v2.models.enums.Status;

import javax.persistence.*;

@Entity
@Table(name = "workers")
@Data
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "surname")
    private String surname;

    @Enumerated(EnumType.STRING)
    private AccessLevel accessLevel;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "number_phone")
    private String numberPhone;

    @Column(name = "fingerprint_hash")
    private String fingerprintHash;

    public String getFullName() {
        // Using same logic as Kursach: "Last F.S."
        if (lastName == null || firstName == null || firstName.isEmpty() || surname == null || surname.isEmpty()) {
            return "";
        }
        return lastName + " " + firstName.charAt(0) + "." + surname.charAt(0) + ".";
    }
}

