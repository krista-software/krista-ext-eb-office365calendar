package app.krista.extensions.essentials.collaboration.eboffice365calendar.impl.transformers;

import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities.Calendar;
import com.microsoft.graph.models.CalendarColor;
import com.microsoft.graph.models.EmailAddress;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalendarTransformerTest {

    @Test
    void transformReturnsNullForNullInput() {
        assertNull(CalendarTransformer.transform(null));
    }

    @Test
    void transformMapsAllFields() {
        com.microsoft.graph.models.Calendar graphCalendar = new com.microsoft.graph.models.Calendar();
        graphCalendar.id = "cal-1";
        graphCalendar.name = "Team Calendar";
        graphCalendar.color = CalendarColor.LIGHT_BLUE;
        graphCalendar.canEdit = true;
        graphCalendar.owner = new EmailAddress();
        graphCalendar.owner.name = "Jane Doe";

        Calendar result = CalendarTransformer.transform(graphCalendar);

        assertEquals("cal-1", result.id);
        assertEquals("Team Calendar", result.name);
        assertEquals("LIGHT_BLUE", result.color);
        assertTrue(result.canEdit);
        assertEquals("Jane Doe", result.owner);
    }

    @Test
    void transformListNeverReturnsNull() {
        assertTrue(CalendarTransformer.transformList(null).isEmpty());
        assertEquals(1, CalendarTransformer.transformList(List.of(new com.microsoft.graph.models.Calendar())).size());
    }
}
