package org.example.acs_v2.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.models.User;
import org.example.acs_v2.models.enums.AccessLevel;
import org.example.acs_v2.services.UserService;
import org.example.acs_v2.utils.ModelAttributeHelper;
import org.example.acs_v2.constants.ViewConstants;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_SECURITY')")
@Slf4j
public class SecurityWorkerController {

    private final UserService userService;
    private final ModelAttributeHelper modelAttributeHelper;

    @GetMapping("/security/workers")
    public String getWorkers(@RequestParam(required = false) String accessLevel, Model model, Principal principal) {
        List<User> workers = userService.list().stream()
                .filter(u -> u.getFirstName() == null || !"Unknown".equalsIgnoreCase(u.getFirstName()))
                .filter(u -> accessLevel == null || accessLevel.isBlank() || "-1".equals(accessLevel)
                        || (accessLevel.matches("\\d+") && u.getUserAccessLvl() != null && u.getUserAccessLvl().name().equalsIgnoreCase("LEVEL_" + accessLevel))
                        || (u.getUserAccessLvl() != null && u.getUserAccessLvl().name().equalsIgnoreCase(accessLevel)))
                .collect(Collectors.toList());

        model.addAttribute("workers", workers);
        model.addAttribute("accessLevels", AccessLevel.values());
        model.addAttribute("accessLevel", accessLevel);
        modelAttributeHelper.addCommonAttributes(model, principal);

        return ViewConstants.SECURITY_WORKERS;
    }
}

