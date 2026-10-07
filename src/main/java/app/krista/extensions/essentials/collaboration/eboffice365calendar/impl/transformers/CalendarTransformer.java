package app.krista.extensions.essentials.collaboration.eboffice365calendar.impl.transformers;

import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities.Calendar;

import java.util.ArrayList;
import java.util.List;

/** Stateless Graph-Calendar -> Calendar entity transformer. null-in -> null-out. */
public final class CalendarTransformer {

    private CalendarTransformer() {
    }

    public static Calendar transform(com.microsoft.graph.models.Calendar graphCalendar) {
        if (graphCalendar == null) {
            return null;
        }
        Calendar c = new Calendar();
        c.id = graphCalendar.id;
        c.name = graphCalendar.name;
        c.color = graphCalendar.color != null ? graphCalendar.color.toString() : null;
        c.canEdit = graphCalendar.canEdit;
        c.owner = graphCalendar.owner != null ? graphCalendar.owner.name : null;
        return c;
    }

    public static List<Calendar> transformList(List<com.microsoft.graph.models.Calendar> graphCalendars) {
        List<Calendar> out = new ArrayList<>();
        if (graphCalendars == null) {
            return out;
        }
        for (com.microsoft.graph.models.Calendar gc : graphCalendars) {
            Calendar t = transform(gc);
            if (t != null) {
                out.add(t);
            }
        }
        return out;
    }
}
