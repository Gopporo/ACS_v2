package org.example.acs_v2;

import org.example.acs_v2.models.User;
import org.example.acs_v2.services.UserService;
import org.example.acs_v2.utils.ModelAttributeHelper;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import java.security.Principal;

import static org.example.acs_v2.constants.ModelAttributeConstants.ERROR_MESSAGE;
import static org.example.acs_v2.constants.ModelAttributeConstants.ROLE;
import static org.example.acs_v2.constants.ModelAttributeConstants.SUCCESS_MESSAGE;
import static org.example.acs_v2.constants.ModelAttributeConstants.USER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ModelAttributeHelperTests {

    @Test
    void addErrorMessage_usesConstantKey() {
        UserService userService = mock(UserService.class);
        ModelAttributeHelper helper = new ModelAttributeHelper(userService);
        Model model = mock(Model.class);

        helper.addErrorMessage(model, "bad");

        verify(model).addAttribute(ERROR_MESSAGE, "bad");
    }

    @Test
    void addSuccessMessage_usesConstantKey() {
        UserService userService = mock(UserService.class);
        ModelAttributeHelper helper = new ModelAttributeHelper(userService);
        Model model = mock(Model.class);

        helper.addSuccessMessage(model, "ok");

        verify(model).addAttribute(SUCCESS_MESSAGE, "ok");
    }

    @Test
    void addUserAttributes_addsUserId_andRole_whenPrincipalPresent() {
        UserService userService = mock(UserService.class);
        ModelAttributeHelper helper = new ModelAttributeHelper(userService);
        Model model = mock(Model.class);
        Principal principal = mock(Principal.class);

        when(userService.getUserId(principal)).thenReturn(1L);
        when(userService.getUserRole(principal)).thenReturn("ROLE_ADMIN");

        helper.addUserAttributes(model, principal);

        verify(model).addAttribute(USER_ID, 1L);
        verify(model).addAttribute(ROLE, "ROLE_ADMIN");
    }

    @Test
    void addUserAttributes_doesNothing_whenPrincipalNull() {
        UserService userService = mock(UserService.class);
        ModelAttributeHelper helper = new ModelAttributeHelper(userService);
        Model model = mock(Model.class);

        helper.addUserAttributes(model, null);

        verifyNoInteractions(userService);
        verifyNoMoreInteractions(model);
    }
}

