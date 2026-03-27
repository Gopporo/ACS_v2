package org.example.acs_v2.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.acs_v2.models.enums.AccessLevel;
import org.example.acs_v2.models.enums.Status;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString(exclude = {"department", "managedDepartment"})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔐 AUTH
    @Column(unique = true)
    private String email;

    @Column(length = 1000)
    private String password;

    private boolean active;
    private boolean approved = false;

    // 👤 ФИО (из Worker)
    private String firstName;
    private String lastName;
    private String surname;

    // 📞 Телефон (объединили)
    @Column(name = "number_phone", unique = true)
    private String numberPhone;

    // 🧬 Биометрия (самое важное)
    @Column(name = "fingerprint_hash")
    private String fingerprintHash;

    @Enumerated(EnumType.STRING)
    private Status status;

    // 📊 Дополнительно
    private String position;

    @Enumerated(EnumType.STRING)
    private AccessLevel userAccessLvl;

    private LocalDateTime dateOfCreated;

    // 🔐 Роли
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    // 🔗 Связи
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToOne(mappedBy = "head")
    private Department managedDepartment;

    // ⏱ Авто-дата
    @PrePersist
    private void init() {
        dateOfCreated = LocalDateTime.now();
    }

    // 🧠 Удобный метод (оставляем)
    public String getFullName() {
        if (lastName == null || firstName == null || surname == null ||
                firstName.isEmpty() || surname.isEmpty()) {
            return "";
        }
        return lastName + " " + firstName.charAt(0) + "." + surname.charAt(0) + ".";
    }

    public String getName() {
        if (lastName == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder(lastName);
        if (firstName != null && !firstName.isBlank()) {
            builder.append(" ").append(firstName);
        }
        if (surname != null && !surname.isBlank()) {
            builder.append(" ").append(surname);
        }
        return builder.toString().trim();
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            this.lastName = "";
            this.firstName = "";
            this.surname = "";
            return;
        }
        String[] parts = name.trim().split("\\s+");
        this.lastName = parts[0];
        this.firstName = parts.length > 1 ? parts[1] : "";
        this.surname = parts.length > 2 ? parts[2] : "";
    }

    public String getNumber_phone() {
        return numberPhone;
    }

    public void setNumber_phone(String number_phone) {
        this.numberPhone = number_phone;
    }

    // 🔐 роли
    public boolean isAdmin() {
        return roles.contains(Role.ROLE_ADMIN);
    }

    public boolean isDirector() {
        return roles.contains(Role.ROLE_DIRECTOR);
    }

    public boolean isSecurity() {
        return roles.contains(Role.ROLE_SECURITY);
    }

    // 🔐 Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
