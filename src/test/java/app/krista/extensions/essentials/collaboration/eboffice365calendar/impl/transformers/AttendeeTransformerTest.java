package app.krista.extensions.essentials.collaboration.eboffice365calendar.impl.transformers;

import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities.Attendee;
import com.microsoft.graph.models.EmailAddress;
import com.microsoft.graph.models.ResponseStatus;
import com.microsoft.graph.models.ResponseType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AttendeeTransformerTest {

    @Test
    void transformReturnsNullForNullInput() {
        assertNull(AttendeeTransformer.transform(null));
    }

    @Test
    void transformMapsEmailDisplayNameAndResponseStatus() {
        com.microsoft.graph.models.Attendee graphAttendee = new com.microsoft.graph.models.Attendee();
        graphAttendee.emailAddress = new EmailAddress();
        graphAttendee.emailAddress.address = "sam@example.com";
        graphAttendee.emailAddress.name = "Sam Lee";
        graphAttendee.status = new ResponseStatus();
        graphAttendee.status.response = ResponseType.ACCEPTED;

        Attendee result = AttendeeTransformer.transform(graphAttendee);

        assertEquals("sam@example.com", result.email);
        assertEquals("Sam Lee", result.displayName);
        assertEquals("sam@example.com", result.id);
        assertEquals("ACCEPTED", result.responseStatus);
    }

    @Test
    void transformListNeverReturnsNullAndSkipsUnmappableEntries() {
        assertTrue(AttendeeTransformer.transformList(null).isEmpty());
        assertEquals(1, AttendeeTransformer.transformList(List.of(new com.microsoft.graph.models.Attendee())).size());
    }

    @Test
    void toContactStringsFormatsDisplayNameAndEmail() {
        com.microsoft.graph.models.Attendee graphAttendee = new com.microsoft.graph.models.Attendee();
        graphAttendee.emailAddress = new EmailAddress();
        graphAttendee.emailAddress.address = "sam@example.com";
        graphAttendee.emailAddress.name = "Sam Lee";

        List<String> contacts = AttendeeTransformer.toContactStrings(List.of(graphAttendee));

        assertEquals(1, contacts.size());
        assertEquals("Sam Lee <sam@example.com>", contacts.get(0));
    }

    @Test
    void toContactStringsSkipsAttendeesWithNoEmail() {
        com.microsoft.graph.models.Attendee graphAttendee = new com.microsoft.graph.models.Attendee();
        assertTrue(AttendeeTransformer.toContactStrings(List.of(graphAttendee)).isEmpty());
    }
}
