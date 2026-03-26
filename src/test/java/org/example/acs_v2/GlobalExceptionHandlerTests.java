package org.example.acs_v2;

import org.example.acs_v2.configurations.GlobalExceptionHandler;
import org.example.acs_v2.exceptions.AccessDeniedException;
import org.example.acs_v2.exceptions.ResourceNotFoundException;
import org.example.acs_v2.exceptions.UserAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import static org.example.acs_v2.constants.ModelAttributeConstants.ERROR_MESSAGE;
import static org.example.acs_v2.constants.ViewConstants.ERROR;
import static org.example.acs_v2.constants.ViewConstants.ERROR_403;
import static org.example.acs_v2.constants.ViewConstants.REGISTRATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class GlobalExceptionHandlerTests {

    @Test
    void handleResourceNotFound_returnsErrorView_andSetsMessage() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        Model model = mock(Model.class);

        ResourceNotFoundException ex = new ResourceNotFoundException("User", 1L);
        String view = handler.handleResourceNotFound(ex, model);

        assertEquals(ERROR, view);
        verify(model).addAttribute(ERROR_MESSAGE, ex.getMessage());
    }

    @Test
    void handleAccessDenied_returns403View_andSetsMessage() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        Model model = mock(Model.class);

        AccessDeniedException ex = new AccessDeniedException("No access");
        String view = handler.handleAccessDenied(ex, model);

        assertEquals(ERROR_403, view);
        verify(model).addAttribute(ERROR_MESSAGE, ex.getMessage());
    }

    @Test
    void handleUserAlreadyExists_returnsRegistrationView_andSetsMessage() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        Model model = mock(Model.class);

        UserAlreadyExistsException ex = new UserAlreadyExistsException("test@example.com");
        String view = handler.handleUserAlreadyExists(ex, model);

        assertEquals(REGISTRATION, view);
        verify(model).addAttribute(ERROR_MESSAGE, ex.getMessage());
    }
}

