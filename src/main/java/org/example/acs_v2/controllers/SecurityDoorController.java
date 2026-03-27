package org.example.acs_v2.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.models.Zone;
import org.example.acs_v2.models.enums.AccessLevel;
import org.example.acs_v2.services.ZoneService;
import org.example.acs_v2.utils.ModelAttributeHelper;
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
public class SecurityDoorController {

    private final ZoneService zoneService;
    private final ModelAttributeHelper modelAttributeHelper;

    @GetMapping("/security/doors")
    public String getDoors(@RequestParam(required = false) String accessLevel, Model model, Principal principal) {
        List<Zone> doors = zoneService.list().stream()
                .filter(z -> accessLevel == null || accessLevel.isBlank() || "-1".equals(accessLevel)
                        || (accessLevel.matches("\\d+") && z.getZoneAccessLvl() != null && z.getZoneAccessLvl().name().equalsIgnoreCase("LEVEL_" + accessLevel))
                        || (z.getZoneAccessLvl() != null && z.getZoneAccessLvl().name().equalsIgnoreCase(accessLevel)))
                .collect(Collectors.toList());

        model.addAttribute("doors", doors);
        model.addAttribute("accessLevels", AccessLevel.values());
        model.addAttribute("accessLevel", accessLevel);
        modelAttributeHelper.addCommonAttributes(model, principal);
        return "security-doors";
    }

}

