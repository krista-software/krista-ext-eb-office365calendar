package app.krista.extensions.essentials.collaboration.eboffice365calendar.impl.transformers;

import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities.EventDetails;
import com.microsoft.graph.models.DateTimeTimeZone;
import com.microsoft.graph.models.Event;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Stateless Graph-Event -> Event Details entity transformer. null-in -> null-out. */
public final class EventDetailsTransformer {

    private EventDetailsTransformer() {
    }

    public static EventDetails transform(Event event) {
        if (event == null) {
            return null;
        }
        EventDetails e = new EventDetails();
        e.eventId = event.id;
        e.title = event.subject;
        e.location = event.location != null ? event.location.displayName : null;
        e.details = event.body != null ? event.body.content : null;
        e.dateFrom = toEpochMillis(event.start);
        e.dateTo = toEpochMillis(event.end);
        e.contacts = AttendeeTransformer.toContactStrings(event.attendees);
        return e;
    }

    public static List<EventDetails> transformList(List<Event> events) {
        List<EventDetails> out = new ArrayList<>();
        if (events == null) {
            return out;
        }
        for (Event ev : events) {
            EventDetails t = transform(ev);
            if (t != null) {
                out.add(t);
            }
        }
        return out;
    }

    /** Graph DateTimeTimeZone (local dateTime string + IANA/Windows zone name) -> epoch millis (UTC). */
    public static Long toEpochMillis(DateTimeTimeZone dtz) {
        if (dtz == null || dtz.dateTime == null || dtz.dateTime.isBlank()) {
            return null;
        }
        try {
            String zone = dtz.timeZone != null && !dtz.timeZone.isBlank() ? dtz.timeZone : "UTC";
            java.time.ZoneId zoneId;
            try {
                zoneId = java.time.ZoneId.of(zone);
            } catch (Exception notFound) {
                zoneId = ZoneOffset.UTC;
            }
            java.time.LocalDateTime local = java.time.LocalDateTime.parse(dtz.dateTime,
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return local.atZone(zoneId).toInstant().toEpochMilli();
        } catch (Exception e) {
            return null;
        }
    }

    /** epoch millis (UTC) -> Graph DateTimeTimeZone (UTC). */
    public static DateTimeTimeZone toGraphDateTime(Long epochMillis) {
        if (epochMillis == null) {
            return null;
        }
        DateTimeTimeZone dtz = new DateTimeTimeZone();
        OffsetDateTime odt = Instant.ofEpochMilli(epochMillis).atOffset(ZoneOffset.UTC);
        dtz.dateTime = odt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        dtz.timeZone = "UTC";
        return dtz;
    }
}
