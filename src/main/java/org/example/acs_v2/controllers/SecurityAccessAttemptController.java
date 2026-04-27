package org.example.acs_v2.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.dto.AccessAttemptDto;
import org.example.acs_v2.models.AccessAttempt;
import org.example.acs_v2.models.User;
import org.example.acs_v2.services.AccessAttemptService;
import org.example.acs_v2.services.UserService;
import org.example.acs_v2.utils.ModelAttributeHelper;
import org.example.acs_v2.constants.ViewConstants;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_SECURITY')")
@Slf4j
public class SecurityAccessAttemptController {

    private final AccessAttemptService accessAttemptService;
    private final UserService userService;
    private final ModelAttributeHelper modelAttributeHelper;

    @GetMapping("/security/accessAttempts")
    public String listAccessAttempts(@RequestParam(required = false) Boolean success,
                                     @RequestParam(required = false) Long userId,
                                     Model model,
                                     java.security.Principal principal) {
        List<AccessAttempt> attempts;
        if (userId != null && success != null) {
            attempts = accessAttemptService.getAttemptsByUserAndSuccess(userId, success);
        } else if (userId != null) {
            attempts = accessAttemptService.getAttemptsByUser(userId);
        } else if (success != null) {
            attempts = accessAttemptService.getAttemptsBySuccess(success);
        } else {
            attempts = accessAttemptService.getAllAttempts();
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        List<AccessAttemptDto> dtos = attempts.stream().map(attempt -> {
            AccessAttemptDto dto = new AccessAttemptDto();
            if (attempt.getTimestamp() != null) {
                dto.setDate(attempt.getTimestamp().format(dateFormatter));
                dto.setTime(attempt.getTimestamp().format(timeFormatter));
            } else {
                dto.setDate("-");
                dto.setTime("-");
            }

            dto.setDoorName(attempt.getZone() != null ? attempt.getZone().getName() : "-");

            User user = attempt.getUser();
            if (user != null) {
                dto.setFullName(user.getFullName());
                dto.setAccessLevel(user.getUserAccessLvl() != null ? user.getUserAccessLvl().name() : "-");
            } else {
                dto.setFullName("-");
                dto.setAccessLevel("-");
            }

            dto.setSuccess(attempt.isSuccess());
            return dto;
        }).toList();

        model.addAttribute("accessAttempts", dtos);
        model.addAttribute("success", success);
        model.addAttribute("selectedUserId", userId);
        model.addAttribute("users", userService.list().stream()
                .filter(u -> u.getFirstName() == null || !"Unknown".equalsIgnoreCase(u.getFirstName()))
                .toList());
        modelAttributeHelper.addCommonAttributes(model, principal);
        return ViewConstants.SECURITY_ACCESS_ATTEMPTS;
    }
}

