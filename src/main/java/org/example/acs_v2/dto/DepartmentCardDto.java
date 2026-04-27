package org.example.acs_v2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DepartmentCardDto {
    private Long id;
    private String name;
    private String headName;
    private long employeeCount;
}

