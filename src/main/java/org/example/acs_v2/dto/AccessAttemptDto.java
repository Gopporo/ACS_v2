package org.example.acs_v2.dto;

import lombok.Data;

@Data
public class AccessAttemptDto {
    private String date;
    private String time;
    private String doorName;
    private String fullName;
    private String accessLevel;
    private boolean success;
}

