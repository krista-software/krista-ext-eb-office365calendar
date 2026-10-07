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

/**
 * Validation is fail-fast, before any Graph call — these tests confirm each required-field check
 * throws IllegalArgumentException without ever asking the Graph client provider for a client.
 */
@ExtendWith(MockitoExtension.class)
class MeetingServiceValidationTest {

    @Mock
    private EBOffice365CalendarGraphClientProvider graphClientProvider;
    @Mock
    private RequestContext requestContext;

    private MeetingService service;

    @BeforeEach
    void setUp() {
        service = new MeetingService(graphClientProvider);
    }

    @Test
    void scheduleMeetingRequiresTitle() {
        assertThrows(IllegalArgumentException.class, () -> service.scheduleMeeting(requestContext, "inv", "e@x.com",
                null, null, "Loc", 1L, 2L, "Details", null));
        verifyNoInteractions(graphClientProvider);
    }

    @Test
    void scheduleMeetingRequiresDates() {
        assertThrows(IllegalArgumentException.class, () -> service.scheduleMeeting(requestContext, "inv", "e@x.com",
                null, "Title", "Loc", null, null, "Details", null));
        verifyNoInteractions(graphClientProvider);
    }

    @Test
    void cancelEventRequiresEventId() {
        assertThrows(IllegalArgumentException.class, () -> service.cancelEvent(requestContext, "inv", "e@x.com", " "));
        verifyNoInteractions(graphClientProvider);
    }

    @Test
    void acceptInvitationRequiresEventId() {
        assertThrows(IllegalArgumentException.class, () -> service.acceptInvitation(requestContext, "inv", "e@x.com", null));
        verifyNoInteractions(graphClientProvider);
    }

    @Test
    void addAttendeesRequiresAttendeeEmail() {
        assertThrows(IllegalArgumentException.class, () -> service.addAttendees(requestContext, "inv", "e@x.com", "evt-1", null));
        verifyNoInteractions(graphClientProvider);
    }

    @Test
    void getNextUpcomingMeetingsRequiresPositiveCount() {
        assertThrows(IllegalArgumentException.class, () -> service.getNextUpcomingMeetings(requestContext, "inv", "e@x.com", 0));
        verifyNoInteractions(graphClientProvider);
    }

    @Test
    void getCalendarViewRequiresBothDates() {
        assertThrows(IllegalArgumentException.class, () -> service.getCalendarView(requestContext, "inv", "e@x.com", null, null, 100L));
        verifyNoInteractions(graphClientProvider);
    }

    @Test
    void getCalendarViewRejectsEndBeforeStart() {
        assertThrows(IllegalArgumentException.class, () -> service.getCalendarView(requestContext, "inv", "e@x.com", null, 200L, 100L));
        verifyNoInteractions(graphClientProvider);
    }

    @Test
    void updateEventRequiresEventId() {
        assertThrows(IllegalArgumentException.class, () -> service.updateEvent(requestContext, "inv", "e@x.com", null,
                "Title", null, null, null, null));
        verifyNoInteractions(graphClientProvider);
    }
}
