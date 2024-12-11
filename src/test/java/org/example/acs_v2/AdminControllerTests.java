package org.example.acs_v2;

import org.example.acs_v2.controllers.AdminController;
import org.example.acs_v2.models.User;
import org.example.acs_v2.models.Zone;
import org.example.acs_v2.services.UserService;
import org.example.acs_v2.services.ZoneService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AdminControllerTests {

    @Mock
    private UserService userService;

    @Mock
    private ZoneService zoneService;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @InjectMocks
    private AdminController adminController;

    public AdminControllerTests() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testManageUsers() {
        // Arrange
        List<User> mockUsers = new ArrayList<>();
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        mockUsers.add(user);

        when(userService.listForAdmin()).thenReturn(mockUsers);
        when(userService.getUserRole(principal)).thenReturn("ROLE_ADMIN");
        when(userService.getUserId(principal)).thenReturn(1L);

        // Act
        String viewName = adminController.manageUsers(model, principal);

        // Assert
        assertEquals("admin-users", viewName);
        verify(model).addAttribute("users", mockUsers);
        verify(model).addAttribute("role", "ROLE_ADMIN");
        verify(model).addAttribute("userId", 1L);
    }

    @Test
    public void testDeleteUser() {
        // Arrange
        Long userId = 1L;
        when(userService.deleteUserById(userId)).thenReturn(true);

        // Act
        String viewName = adminController.deleteUser(userId, model);

        // Assert
        assertEquals("redirect:/admin/preregistration", viewName);
        verify(userService).deleteUserById(userId);
    }

    @Test
    public void testCreateZone() {
        // Arrange
        Zone mockZone = new Zone();
        mockZone.setName("Test Zone");

        when(zoneService.createZone(mockZone)).thenReturn(true);

        // Act
        String viewName = adminController.createZone(mockZone, model);

        // Assert
        assertEquals("redirect:/admin/zones", viewName);
        verify(zoneService).createZone(mockZone);
    }

    @Test
    public void testToggleUserBlock() {
        // Arrange
        Long userId = 1L;

        doNothing().when(userService).toggleUserActiveStatus(userId);

        // Act
        String viewName = adminController.toggleUserBlock(userId);

        // Assert
        assertEquals("redirect:/admin/users", viewName);
        verify(userService).toggleUserActiveStatus(userId);
    }

    @Test
    public void testGetUsers() {
        // Arrange
        int accessLevel = 3;
        List<User> mockUsers = new ArrayList<>();
        User user = new User();
        user.setId(1L);
        user.setUserAccessLvl(accessLevel);
        mockUsers.add(user);

        when(userService.getUsersByAccessLvl(accessLevel)).thenReturn(mockUsers);
        when(userService.getUserRole(principal)).thenReturn("ROLE_ADMIN");
        when(userService.getUserId(principal)).thenReturn(1L);

        // Act
        String viewName = adminController.getUsers(accessLevel, model, principal);

        // Assert
        assertEquals("admin-users", viewName);
        verify(model).addAttribute("users", mockUsers);
        verify(model).addAttribute("role", "ROLE_ADMIN");
        verify(model).addAttribute("userId", 1L);
    }

    @Test
    public void testGetZones() {
        // Arrange
        int accessLevel = 2;
        List<Zone> mockZones = new ArrayList<>();
        Zone zone = new Zone();
        zone.setZoneAccessLvl(accessLevel);
        mockZones.add(zone);

        when(zoneService.getZonesByAccessLvl(accessLevel)).thenReturn(mockZones);
        when(userService.getUserRole(principal)).thenReturn("ROLE_ADMIN");
        when(userService.getUserId(principal)).thenReturn(1L);

        // Act
        String viewName = adminController.getZones(accessLevel, model, principal);

        // Assert
        assertEquals("admin-zones", viewName);
        verify(model).addAttribute("zones", mockZones);
        verify(model).addAttribute("role", "ROLE_ADMIN");
        verify(model).addAttribute("userId", 1L);
    }
}
