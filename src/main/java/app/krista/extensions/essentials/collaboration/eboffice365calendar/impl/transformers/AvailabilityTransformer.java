package app.krista.extensions.essentials.collaboration.eboffice365calendar.impl.transformers;

import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities.Availability;
import com.microsoft.graph.models.ScheduleInformation;
import com.microsoft.graph.models.ScheduleItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Stateless Graph-ScheduleInformation -> Availability entity transformer. Flattens each schedule's
 * list of busy/tentative/oof slots into one Availability row per slot; a mailbox with no scheduleItems
 * (fully free in the requested range) yields no rows for that mailbox.
 */
public final class AvailabilityTransformer {

    private AvailabilityTransformer() {
    }

    public static List<Availability> transform(ScheduleInformation schedule) {
        List<Availability> out = new ArrayList<>();
        if (schedule == null) {
            return out;
        }
        String availabilityView = schedule.availabilityView;
        if (schedule.scheduleItems == null || schedule.scheduleItems.isEmpty()) {
            return out;
        }
        for (ScheduleItem item : schedule.scheduleItems) {
            Availability a = new Availability();
            a.scheduleId = schedule.scheduleId;
            a.status = item.status != null ? item.status.toString() : null;
            a.start = EventDetailsTransformer.toEpochMillis(item.start);
            a.end = EventDetailsTransformer.toEpochMillis(item.end);
            a.availabilityView = availabilityView;
            out.add(a);
        }
        return out;
    }

    public static List<Availability> transformList(List<ScheduleInformation> schedules) {
        List<Availability> out = new ArrayList<>();
        if (schedules == null) {
            return out;
        }
        for (ScheduleInformation s : schedules) {
            out.addAll(transform(s));
        }
        return out;
    }
}
