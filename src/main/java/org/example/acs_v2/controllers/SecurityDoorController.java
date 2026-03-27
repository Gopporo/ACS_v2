package org.example.acs_v2.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.exceptions.ResourceNotFoundException;
import org.example.acs_v2.models.Zone;
import org.example.acs_v2.models.enums.AccessLevel;
import org.example.acs_v2.services.ZoneService;
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
public class SecurityDoorController {

    private final ZoneService zoneService;
    private final ModelAttributeHelper modelAttributeHelper;

    @GetMapping("/security/doors")
    public String getDoors(@RequestParam(required = false) String accessLevel, Model model, Principal principal) {
        List<Zone> doors = zoneService.list().stream()
                .filter(z -> accessLevel == null || accessLevel.isBlank() || "-1".equals(accessLevel)
                        || (z.getZoneAccessLvl() != null && z.getZoneAccessLvl().name().equalsIgnoreCase(accessLevel)))
                .collect(Collectors.toList());

        model.addAttribute("doors", doors);
        model.addAttribute("accessLevels", AccessLevel.values());
        model.addAttribute("accessLevel", accessLevel);
        modelAttributeHelper.addCommonAttributes(model, principal);
        return ViewConstants.SECURITY_DOORS;
    }

    @GetMapping("/security/doors/add")
    public String showAddDoorForm(Model model, Principal principal) {
        Zone door = new Zone();
        model.addAttribute("door", door);
        model.addAttribute("accessLevels", AccessLevel.values());
        modelAttributeHelper.addCommonAttributes(model, principal);
        return ViewConstants.SECURITY_DOOR_ADD;
    }

    @PostMapping("/security/doors/add")
    public String addDoor(@ModelAttribute Zone door) {
        if (door.getZoneAccessLvl() == null) {
            door.setZoneAccessLvl(AccessLevel.LEVEL_1);
        }
        zoneService.createZone(door);
        return "redirect:/security/doors";
    }

    @GetMapping("/security/doors/edit/{id}")
    public String showEditDoorForm(@PathVariable Long id, Model model, Principal principal) {
        Zone door = zoneService.getById(id);
        if (door == null) {
            throw new ResourceNotFoundException("Zone", id);
        }
        model.addAttribute("door", door);
        model.addAttribute("accessLevels", AccessLevel.values());
        modelAttributeHelper.addCommonAttributes(model, principal);
        return ViewConstants.SECURITY_DOOR_EDIT;
    }

    @PostMapping("/security/doors/update/{id}")
    public String updateDoor(@PathVariable Long id, @ModelAttribute Zone formDoor) {
        Zone zone = zoneService.getById(id);
        zone.setName(formDoor.getName());
        zone.setDisc(formDoor.getDisc());
        zone.setZoneAccessLvl(formDoor.getZoneAccessLvl());
        zoneService.save(zone);
        return "redirect:/security/doors/edit/" + id;
    }

    @GetMapping("/security/doors/delete/{id}")
    public String deleteDoor(@PathVariable Long id) {
        zoneService.deleteById(id);
        return "redirect:/security/doors";
    }
}

