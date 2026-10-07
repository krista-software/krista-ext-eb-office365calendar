package app.krista.extensions.essentials.collaboration.eboffice365calendar.impl.transformers;

import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities.Availability;
import com.microsoft.graph.models.DateTimeTimeZone;
import com.microsoft.graph.models.FreeBusyStatus;
import com.microsoft.graph.models.ScheduleInformation;
import com.microsoft.graph.models.ScheduleItem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AvailabilityTransformerTest {

    @Test
    void transformReturnsEmptyListForNullSchedule() {
        assertTrue(AvailabilityTransformer.transform(null).isEmpty());
    }

    @Test
    void transformReturnsEmptyListWhenNoScheduleItems() {
        ScheduleInformation schedule = new ScheduleInformation();
        schedule.scheduleId = "jane@example.com";
        assertTrue(AvailabilityTransformer.transform(schedule).isEmpty());
    }

    @Test
    void transformFlattensOneRowPerScheduleItem() {
        ScheduleInformation schedule = new ScheduleInformation();
        schedule.scheduleId = "jane@example.com";
        schedule.availabilityView = "012";

        ScheduleItem busy = new ScheduleItem();
        busy.status = FreeBusyStatus.BUSY;
        busy.start = dtz("2026-01-15T09:00:00");
        busy.end = dtz("2026-01-15T10:00:00");
        schedule.scheduleItems = List.of(busy);

        List<Availability> result = AvailabilityTransformer.transform(schedule);

        assertEquals(1, result.size());
        assertEquals("jane@example.com", result.get(0).scheduleId);
        assertEquals("BUSY", result.get(0).status);
        assertEquals("012", result.get(0).availabilityView);
        assertNotNull(result.get(0).start);
        assertNotNull(result.get(0).end);
    }

    @Test
    void transformListNeverReturnsNull() {
        assertTrue(AvailabilityTransformer.transformList(null).isEmpty());
    }

    private static DateTimeTimeZone dtz(String dateTime) {
        DateTimeTimeZone d = new DateTimeTimeZone();
        d.dateTime = dateTime;
        d.timeZone = "UTC";
        return d;
    }
}
