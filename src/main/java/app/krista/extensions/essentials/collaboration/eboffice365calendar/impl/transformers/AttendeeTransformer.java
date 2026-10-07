package app.krista.extensions.essentials.collaboration.eboffice365calendar.impl.transformers;

import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities.Attendee;
import com.microsoft.graph.models.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

/** Stateless Graph-attendee -> Attendee entity transformer. null-in -> null-out. */
public final class AttendeeTransformer {

    private AttendeeTransformer() {
    }

    public static Attendee transform(com.microsoft.graph.models.Attendee graphAttendee) {
        if (graphAttendee == null) {
            return null;
        }
        Attendee attendee = new Attendee();
        if (graphAttendee.emailAddress != null) {
            attendee.email = graphAttendee.emailAddress.address;
            attendee.displayName = graphAttendee.emailAddress.name;
            attendee.id = graphAttendee.emailAddress.address;
        }
        ResponseStatus status = graphAttendee.status;
        attendee.responseStatus = status != null && status.response != null ? status.response.toString() : null;
        return attendee;
    }

    public static List<Attendee> transformList(List<com.microsoft.graph.models.Attendee> graphAttendees) {
        List<Attendee> out = new ArrayList<>();
        if (graphAttendees == null) {
            return out;
        }
        for (com.microsoft.graph.models.Attendee a : graphAttendees) {
            Attendee t = transform(a);
            if (t != null) {
                out.add(t);
            }
        }
        return out;
    }

    /** "Display Name <email>" strings for Event Details' flattened "Contacts" field. */
    public static List<String> toContactStrings(List<com.microsoft.graph.models.Attendee> graphAttendees) {
        List<String> out = new ArrayList<>();
        for (Attendee a : transformList(graphAttendees)) {
            if (a.email == null) {
                continue;
            }
            out.add((a.displayName != null && !a.displayName.isBlank() ? a.displayName + " " : "") + "<" + a.email + ">");
        }
        return out;
    }
}
