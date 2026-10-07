package app.krista.extensions.essentials.collaboration.eboffice365calendar.service;

import app.krista.extensions.essentials.collaboration.eboffice365calendar.integration.EBOffice365CalendarGraphClientProvider;
import app.krista.ksdk.context.RequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceValidationTest {

    @Mock
    private EBOffice365CalendarGraphClientProvider graphClientProvider;
    @Mock
    private RequestContext requestContext;

    private AvailabilityService service;

    @BeforeEach
    void setUp() {
        service = new AvailabilityService(graphClientProvider);
    }

    @Test
    void requiresAtLeastOneEmailAddress() {
        assertThrows(IllegalArgumentException.class, () -> service.getFreeBusySchedule(requestContext, "inv", "e@x.com",
                Collections.emptyList(), 1L, 2L, null));
        verifyNoInteractions(graphClientProvider);
    }

    @Test
    void requiresBothDates() {
        assertThrows(IllegalArgumentException.class, () -> service.getFreeBusySchedule(requestContext, "inv", "e@x.com",
                List.of("jane@example.com"), null, null, null));
        verifyNoInteractions(graphClientProvider);
    }

    @Test
    void rejectsEndBeforeStart() {
        assertThrows(IllegalArgumentException.class, () -> service.getFreeBusySchedule(requestContext, "inv", "e@x.com",
                List.of("jane@example.com"), 200L, 100L, null));
        verifyNoInteractions(graphClientProvider);
    }
}
