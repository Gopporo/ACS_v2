package org.example.acs_v2.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.exceptions.ResourceNotFoundException;
import org.example.acs_v2.models.Door;
import org.example.acs_v2.models.enums.AccessLevel;
import org.example.acs_v2.services.DoorService;
import org.example.acs_v2.utils.ModelAttributeHelper;
import org.example.acs_v2.constants.ViewConstants;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_SECURITY')")
@Slf4j
public class SecurityDoorController {

    private final DoorService doorService;
    private final ModelAttributeHelper modelAttributeHelper;

    @GetMapping("/security/doors")
    public String getDoors(@RequestParam(required = false) String accessLevel, Model model, Principal principal) {
        List<Door> doors = (accessLevel == null || accessLevel.isBlank())
                ? doorService.getAllDoors()
                : doorService.getDoorsByAccessLevel(accessLevel);

        model.addAttribute("doors", doors);
        model.addAttribute("accessLevels", AccessLevel.values());
        model.addAttribute("accessLevel", accessLevel);
        modelAttributeHelper.addCommonAttributes(model, principal);
        return ViewConstants.SECURITY_DOORS;
    }

    @GetMapping("/security/doors/add")
    public String showAddDoorForm(Model model, Principal principal) {
        Door door = new Door();
        model.addAttribute("door", door);
        model.addAttribute("accessLevels", AccessLevel.values());
        modelAttributeHelper.addCommonAttributes(model, principal);
        return ViewConstants.SECURITY_DOOR_ADD;
    }

    @PostMapping("/security/doors/add")
    public String addDoor(@ModelAttribute Door door) {
        if (door.getAccessLevel() == null) {
            door.setAccessLevel(AccessLevel.UNKNOWN);
        }
        doorService.addDoor(door);
        return "redirect:/security/doors";
    }

    @GetMapping("/security/doors/edit/{id}")
    public String showEditDoorForm(@PathVariable Long id, Model model, Principal principal) {
        Door door = doorService.getById(id);
        if (door == null) {
            throw new ResourceNotFoundException("Door", id);
        }
        model.addAttribute("door", door);
        model.addAttribute("accessLevels", AccessLevel.values());
        modelAttributeHelper.addCommonAttributes(model, principal);
        return ViewConstants.SECURITY_DOOR_EDIT;
    }

    @PostMapping("/security/doors/update/{id}")
    public String updateDoor(@PathVariable Long id, @ModelAttribute Door formDoor) {
        doorService.updateDoor(id, formDoor);
        return "redirect:/security/doors/edit/" + id;
    }

    @GetMapping("/security/doors/delete/{id}")
    public String deleteDoor(@PathVariable Long id) {
        doorService.deleteDoor(id);
        return "redirect:/security/doors";
    }
}

