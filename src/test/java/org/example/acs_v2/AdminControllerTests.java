package org.example.acs_v2;

import org.example.acs_v2.controllers.AdminController;
import org.example.acs_v2.models.User;
import org.example.acs_v2.models.Zone;
import org.example.acs_v2.repositories.UserRepository;
import org.example.acs_v2.services.DepartmentService;
import org.example.acs_v2.services.UserService;
import org.example.acs_v2.services.ZoneService;
import org.example.acs_v2.utils.ModelAttributeHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTests {

    @Mock
    private UserService userService;

    @Mock
    private ZoneService zoneService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DepartmentService departmentService;

    @Mock
    private ModelAttributeHelper modelAttributeHelper;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @InjectMocks
    private AdminController adminController;

    @Test
    public void testManageUsers() {

        List<User> mockUsers = new ArrayList<>();
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        mockUsers.add(user);

        when(userService.listForAdmin()).thenReturn(mockUsers);
        String viewName = adminController.manageUsers(model, principal);

        assertEquals("admin-users", viewName);
        verify(model).addAttribute("users", mockUsers);
        verify(modelAttributeHelper).addUserAttributes(model, principal);
    }

    @Test
    public void testDeleteUser() {
        Long userId = 1L;
        doNothing().when(userService).deleteUserById(userId);


        String viewName = adminController.deleteUser(userId, model);

        assertEquals("redirect:/admin/preregistration", viewName);
        verify(userService).deleteUserById(userId);
    }

    @Test
    public void testCreateZone() {
        Zone mockZone = new Zone();
        mockZone.setName("Test Zone");

        when(zoneService.createZone(mockZone)).thenReturn(true);

        String viewName = adminController.createZone(mockZone, model);

        assertEquals("redirect:/admin/zones", viewName);
        verify(zoneService).createZone(mockZone);
    }

    @Test
    public void testToggleUserBlock() {
        Long userId = 1L;

        doNothing().when(userService).toggleUserActiveStatus(userId);

        String viewName = adminController.toggleUserBlock(userId);

        assertEquals("redirect:/admin/users", viewName);
        verify(userService).toggleUserActiveStatus(userId);
    }

    @Test
    public void testGetUsers() {
        int accessLevel = 3;
        List<User> mockUsers = new ArrayList<>();
        User user = new User();
        user.setId(1L);
        user.setUserAccessLvl(accessLevel);
        mockUsers.add(user);

        when(userService.getUsersByAccessLvl(accessLevel)).thenReturn(mockUsers);
        String viewName = adminController.getUsers(accessLevel, model, principal);

        assertEquals("admin-users", viewName);
        verify(model).addAttribute("users", mockUsers);
        verify(modelAttributeHelper).addUserAttributes(model, principal);
    }

    @Test
    public void testGetZones() {
        int accessLevel = 2;
        List<Zone> mockZones = new ArrayList<>();
        Zone zone = new Zone();
        zone.setZoneAccessLvl(accessLevel);
        mockZones.add(zone);

        when(zoneService.getZonesByAccessLvl(accessLevel)).thenReturn(mockZones);
        String viewName = adminController.getZones(accessLevel, model, principal);

        assertEquals("admin-zones", viewName);
        verify(model).addAttribute("zones", mockZones);
        verify(modelAttributeHelper).addUserAttributes(model, principal);
    }
}
