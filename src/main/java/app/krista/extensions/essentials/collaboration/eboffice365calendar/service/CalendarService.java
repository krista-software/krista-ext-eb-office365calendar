package app.krista.extensions.essentials.collaboration.eboffice365calendar.service;

import app.krista.extensions.essentials.collaboration.eboffice365calendar.integration.EBOffice365CalendarGraphClientProvider;
import app.krista.ksdk.context.RequestContext;
import com.microsoft.graph.models.Calendar;
import com.microsoft.graph.requests.GraphServiceClient;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

/**
 * Orchestrates Calendars-area Graph calls: resolve the operator's Graph client, call, return the raw
 * Graph model (transformed to an entity one layer up, in CalendarsArea).
 */
@Service
public class CalendarService {

    private final EBOffice365CalendarGraphClientProvider graphClientProvider;

    @Inject
    public CalendarService(EBOffice365CalendarGraphClientProvider graphClientProvider) {
        this.graphClientProvider = graphClientProvider;
    }

    public List<Calendar> listCalendars(RequestContext requestContext, String invokerId, String email) throws IOException {
        GraphServiceClient<okhttp3.Request> client = graphClientProvider.forRequest(requestContext, invokerId);
        return client.users(email).calendars().buildRequest().get().getCurrentPage();
    }

    public Calendar getCalendar(RequestContext requestContext, String invokerId, String email, String calendarId) throws IOException {
        if (calendarId == null || calendarId.isBlank()) {
            throw new IllegalArgumentException("Calendar Id is required.");
        }
        GraphServiceClient<okhttp3.Request> client = graphClientProvider.forRequest(requestContext, invokerId);
        return client.users(email).calendars(calendarId).buildRequest().get();
    }

    public Calendar createCalendar(RequestContext requestContext, String invokerId, String email, String name) throws IOException {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required.");
        }
        Calendar calendar = new Calendar();
        calendar.name = name;
        GraphServiceClient<okhttp3.Request> client = graphClientProvider.forRequest(requestContext, invokerId);
        return client.users(email).calendars().buildRequest().post(calendar);
    }
}
