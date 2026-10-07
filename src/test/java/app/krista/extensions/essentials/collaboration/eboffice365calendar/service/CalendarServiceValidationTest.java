package app.krista.extensions.essentials.collaboration.eboffice365calendar.service;

import app.krista.extensions.essentials.collaboration.eboffice365calendar.integration.EBOffice365CalendarGraphClientProvider;
import app.krista.ksdk.context.RequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class CalendarServiceValidationTest {

    @Mock
    private EBOffice365CalendarGraphClientProvider graphClientProvider;
    @Mock
    private RequestContext requestContext;

    private CalendarService service;

    @BeforeEach
    void setUp() {
        service = new CalendarService(graphClientProvider);
    }

    @Test
    void getCalendarRequiresCalendarId() {
        assertThrows(IllegalArgumentException.class, () -> service.getCalendar(requestContext, "inv", "e@x.com", null));
        verifyNoInteractions(graphClientProvider);
    }

    @Test
    void createCalendarRequiresName() {
        assertThrows(IllegalArgumentException.class, () -> service.createCalendar(requestContext, "inv", "e@x.com", " "));
        verifyNoInteractions(graphClientProvider);
    }
}
