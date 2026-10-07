package app.krista.extensions.essentials.collaboration.eboffice365calendar.impl.transformers;

import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities.EventDetails;
import com.microsoft.graph.models.Attendee;
import com.microsoft.graph.models.DateTimeTimeZone;
import com.microsoft.graph.models.EmailAddress;
import com.microsoft.graph.models.Event;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.models.Location;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventDetailsTransformerTest {

    @Test
    void transformReturnsNullForNullInput() {
        assertNull(EventDetailsTransformer.transform(null));
    }

    @Test
    void transformMapsAllFields() {
        Event event = new Event();
        event.id = "evt-1";
        event.subject = "Planning Sync";
        event.location = new Location();
        event.location.displayName = "Room 42";
        event.body = new ItemBody();
        event.body.content = "Quarterly planning";
        event.start = dtz("2026-01-15T09:00:00", "UTC");
        event.end = dtz("2026-01-15T10:00:00", "UTC");

        Attendee a = new Attendee();
        a.emailAddress = new EmailAddress();
        a.emailAddress.address = "jane@example.com";
        a.emailAddress.name = "Jane Doe";
        event.attendees = List.of(a);

        EventDetails result = EventDetailsTransformer.transform(event);

        assertEquals("evt-1", result.eventId);
        assertEquals("Planning Sync", result.title);
        assertEquals("Room 42", result.location);
        assertEquals("Quarterly planning", result.details);
        assertEquals(1, result.contacts.size());
        assertTrue(result.contacts.get(0).contains("jane@example.com"));
        assertEquals(expectedEpochMillisUtc("2026-01-15T09:00:00"), result.dateFrom);
        assertEquals(expectedEpochMillisUtc("2026-01-15T10:00:00"), result.dateTo);
    }

    private static long expectedEpochMillisUtc(String isoLocalDateTime) {
        return java.time.LocalDateTime.parse(isoLocalDateTime)
                .atZone(java.time.ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    @Test
    void transformListSkipsNullsAndNeverReturnsNull() {
        assertTrue(EventDetailsTransformer.transformList(null).isEmpty());
        assertEquals(1, EventDetailsTransformer.transformList(List.of(new Event())).size());
    }

    @Test
    void epochMillisRoundTripsThroughGraphDateTime() {
        long original = 1768467600000L;
        DateTimeTimeZone dtz = EventDetailsTransformer.toGraphDateTime(original);
        assertEquals("UTC", dtz.timeZone);
        Long roundTripped = EventDetailsTransformer.toEpochMillis(dtz);
        assertEquals(original, roundTripped);
    }

    @Test
    void toEpochMillisReturnsNullForBlankOrMissingDateTime() {
        assertNull(EventDetailsTransformer.toEpochMillis(null));
        assertNull(EventDetailsTransformer.toEpochMillis(dtz("", "UTC")));
    }

    private static DateTimeTimeZone dtz(String dateTime, String zone) {
        DateTimeTimeZone d = new DateTimeTimeZone();
        d.dateTime = dateTime;
        d.timeZone = zone;
        return d;
    }
}
