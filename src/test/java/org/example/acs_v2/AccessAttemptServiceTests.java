package org.example.acs_v2;

import org.example.acs_v2.repositories.AccessAttemptRepository;
import org.example.acs_v2.services.AccessAttemptService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccessAttemptServiceTests {

    @Mock
    private AccessAttemptRepository accessAttemptRepository;

    @InjectMocks
    private AccessAttemptService accessAttemptService;

    @Test
    void getAllAttempts_usesDescendingOrderRepositoryMethod() {
        accessAttemptService.getAllAttempts();
        verify(accessAttemptRepository).findAllByOrderByTimestampDesc();
    }

    @Test
    void getAttemptsByUserAndSuccess_usesDescendingOrderRepositoryMethod() {
        accessAttemptService.getAttemptsByUserAndSuccess(42L, true);
        verify(accessAttemptRepository).findByUserIdAndSuccessOrderByTimestampDesc(42L, true);
    }
}
