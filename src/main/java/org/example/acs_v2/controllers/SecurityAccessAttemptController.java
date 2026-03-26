package org.example.acs_v2.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.dto.AccessAttemptDto;
import org.example.acs_v2.models.AccessAttempt;
import org.example.acs_v2.models.Worker;
import org.example.acs_v2.services.AccessAttemptService;
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
    private final ModelAttributeHelper modelAttributeHelper;

    @GetMapping("/security/accessAttempts")
    public String listAccessAttempts(@RequestParam(required = false) Boolean success, Model model, java.security.Principal principal) {
        List<AccessAttempt> attempts = (success == null)
                ? accessAttemptService.getAllAttempts()
                : accessAttemptService.getAttemptsBySuccess(success);

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

            dto.setDoorName(attempt.getDoor() != null ? attempt.getDoor().getName() : "-");

            Worker worker = attempt.getWorker();
            if (worker != null) {
                dto.setFullName(worker.getFullName());
                dto.setAccessLevel(worker.getAccessLevel() != null ? worker.getAccessLevel().name() : "-");
            } else {
                dto.setFullName("-");
                dto.setAccessLevel("-");
            }

            dto.setSuccess(attempt.isSuccess());
            return dto;
        }).toList();

        model.addAttribute("accessAttempts", dtos);
        model.addAttribute("success", success);
        modelAttributeHelper.addCommonAttributes(model, principal);
        return ViewConstants.SECURITY_ACCESS_ATTEMPTS;
    }
}

