package app.krista.extensions.essentials.collaboration.eboffice365calendar.service;

import app.krista.extensions.essentials.collaboration.eboffice365calendar.impl.transformers.EventDetailsTransformer;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.integration.EBOffice365CalendarGraphClientProvider;
import app.krista.ksdk.context.RequestContext;
import com.microsoft.graph.models.Attendee;
import com.microsoft.graph.models.AttendeeType;
import com.microsoft.graph.models.EmailAddress;
import com.microsoft.graph.models.Event;
import com.microsoft.graph.models.EventAcceptParameterSet;
import com.microsoft.graph.models.EventCancelParameterSet;
import com.microsoft.graph.models.EventDeclineParameterSet;
import com.microsoft.graph.models.EventTentativelyAcceptParameterSet;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.models.Location;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.GraphServiceClient;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Orchestrates Meetings-area Graph calls. */
@Service
public class MeetingService {

    private final EBOffice365CalendarGraphClientProvider graphClientProvider;

    @Inject
    public MeetingService(EBOffice365CalendarGraphClientProvider graphClientProvider) {
        this.graphClientProvider = graphClientProvider;
    }

    private GraphServiceClient<okhttp3.Request> client(RequestContext ctx, String invokerId) throws IOException {
        return graphClientProvider.forRequest(ctx, invokerId);
    }

    public Event scheduleMeeting(RequestContext ctx, String invokerId, String email, String calendarId,
                                  String title, String location, Long dateFrom, Long dateTo, String details,
                                  String attendeeEmail) throws IOException {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title is required.");
        }
        if (dateFrom == null || dateTo == null) {
            throw new IllegalArgumentException("Date From and Date to are required.");
        }
        Event event = new Event();
        event.subject = title;
        if (location != null && !location.isBlank()) {
            event.location = new Location();
            event.location.displayName = location;
        }
        if (details != null && !details.isBlank()) {
            event.body = new ItemBody();
            event.body.contentType = BodyType.TEXT;
            event.body.content = details;
        }
        event.start = EventDetailsTransformer.toGraphDateTime(dateFrom);
        event.end = EventDetailsTransformer.toGraphDateTime(dateTo);
        if (attendeeEmail != null && !attendeeEmail.isBlank()) {
            event.attendees = List.of(buildAttendee(attendeeEmail, AttendeeType.REQUIRED));
        }
        GraphServiceClient<okhttp3.Request> graphClient = client(ctx, invokerId);
        if (calendarId != null && !calendarId.isBlank()) {
            return graphClient.users(email).calendars(calendarId).events().buildRequest().post(event);
        }
        return graphClient.users(email).events().buildRequest().post(event);
    }

    public Event cancelEvent(RequestContext ctx, String invokerId, String email, String eventId) throws IOException {
        requireEventId(eventId);
        GraphServiceClient<okhttp3.Request> graphClient = client(ctx, invokerId);
        graphClient.users(email).events(eventId)
                .cancel(EventCancelParameterSet.newBuilder().build())
                .buildRequest().post();
        return getEventQuietly(graphClient, email, eventId);
    }

    public Event acceptInvitation(RequestContext ctx, String invokerId, String email, String eventId) throws IOException {
        requireEventId(eventId);
        GraphServiceClient<okhttp3.Request> graphClient = client(ctx, invokerId);
        graphClient.users(email).events(eventId)
                .accept(EventAcceptParameterSet.newBuilder().withSendResponse(true).build())
                .buildRequest().post();
        return getEventQuietly(graphClient, email, eventId);
    }

    public Event declineInvitation(RequestContext ctx, String invokerId, String email, String eventId) throws IOException {
        requireEventId(eventId);
        GraphServiceClient<okhttp3.Request> graphClient = client(ctx, invokerId);
        graphClient.users(email).events(eventId)
                .decline(EventDeclineParameterSet.newBuilder().withSendResponse(true).build())
                .buildRequest().post();
        return getEventQuietly(graphClient, email, eventId);
    }

    public Event tentativeInvitation(RequestContext ctx, String invokerId, String email, String eventId) throws IOException {
        requireEventId(eventId);
        GraphServiceClient<okhttp3.Request> graphClient = client(ctx, invokerId);
        graphClient.users(email).events(eventId)
                .tentativelyAccept(EventTentativelyAcceptParameterSet.newBuilder().withSendResponse(true).build())
                .buildRequest().post();
        return getEventQuietly(graphClient, email, eventId);
    }

    public Event addAttendees(RequestContext ctx, String invokerId, String email, String eventId, String attendeeEmail) throws IOException {
        requireEventId(eventId);
        if (attendeeEmail == null || attendeeEmail.isBlank()) {
            throw new IllegalArgumentException("Attendee Email is required.");
        }
        GraphServiceClient<okhttp3.Request> graphClient = client(ctx, invokerId);
        Event existing = graphClient.users(email).events(eventId).buildRequest().get();
        List<Attendee> attendees = existing.attendees != null ? new ArrayList<>(existing.attendees) : new ArrayList<>();
        boolean alreadyPresent = attendees.stream().anyMatch(a -> a.emailAddress != null
                && attendeeEmail.equalsIgnoreCase(a.emailAddress.address));
        if (!alreadyPresent) {
            attendees.add(buildAttendee(attendeeEmail, AttendeeType.REQUIRED));
        }
        Event patch = new Event();
        patch.attendees = attendees;
        return graphClient.users(email).events(eventId).buildRequest().patch(patch);
    }

    public Event removeAttendee(RequestContext ctx, String invokerId, String email, String eventId, String attendeeEmail) throws IOException {
        requireEventId(eventId);
        if (attendeeEmail == null || attendeeEmail.isBlank()) {
            throw new IllegalArgumentException("Attendee Email is required.");
        }
        GraphServiceClient<okhttp3.Request> graphClient = client(ctx, invokerId);
        Event existing = graphClient.users(email).events(eventId).buildRequest().get();
        List<Attendee> attendees = new ArrayList<>();
        if (existing.attendees != null) {
            for (Attendee a : existing.attendees) {
                if (a.emailAddress == null || !attendeeEmail.equalsIgnoreCase(a.emailAddress.address)) {
                    attendees.add(a);
                }
            }
        }
        Event patch = new Event();
        patch.attendees = attendees;
        return graphClient.users(email).events(eventId).buildRequest().patch(patch);
    }

    public List<Event> getNextUpcomingMeetings(RequestContext ctx, String invokerId, String email, int count) throws IOException {
        if (count <= 0) {
            throw new IllegalArgumentException("Number of meeting must be greater than zero.");
        }
        GraphServiceClient<okhttp3.Request> graphClient = client(ctx, invokerId);
        String nowIso = java.time.Instant.now().toString();
        List<QueryOption> options = List.of(
                new QueryOption("$filter", "start/dateTime ge '" + nowIso + "'"),
                new QueryOption("$orderby", "start/dateTime"),
                new QueryOption("$top", String.valueOf(count)));
        return graphClient.users(email).events().buildRequest(options).get().getCurrentPage();
    }

    public List<Event> getAllUnacceptedEvents(RequestContext ctx, String invokerId, String email) throws IOException {
        GraphServiceClient<okhttp3.Request> graphClient = client(ctx, invokerId);
        List<QueryOption> options = List.of(new QueryOption("$filter", "responseStatus/response eq 'notResponded'"));
        return graphClient.users(email).events().buildRequest(options).get().getCurrentPage();
    }

    public List<Event> getCalendarView(RequestContext ctx, String invokerId, String email, String calendarId,
                                        Long startDateTime, Long endDateTime) throws IOException {
        if (startDateTime == null || endDateTime == null) {
            throw new IllegalArgumentException("Start Date Time and End Date Time are required.");
        }
        if (endDateTime < startDateTime) {
            throw new IllegalArgumentException("End Date Time must be after Start Date Time.");
        }
        GraphServiceClient<okhttp3.Request> graphClient = client(ctx, invokerId);
        List<QueryOption> options = List.of(
                new QueryOption("startDateTime", EventDetailsTransformer.toGraphDateTime(startDateTime).dateTime + "Z"),
                new QueryOption("endDateTime", EventDetailsTransformer.toGraphDateTime(endDateTime).dateTime + "Z"));
        if (calendarId != null && !calendarId.isBlank()) {
            return graphClient.users(email).calendars(calendarId).calendarView().buildRequest(options).get().getCurrentPage();
        }
        return graphClient.users(email).calendarView().buildRequest(options).get().getCurrentPage();
    }

    public Event updateEvent(RequestContext ctx, String invokerId, String email, String eventId, String title,
                              String location, String details, Long dateFrom, Long dateTo) throws IOException {
        requireEventId(eventId);
        Event patch = new Event();
        if (title != null && !title.isBlank()) {
            patch.subject = title;
        }
        if (location != null && !location.isBlank()) {
            patch.location = new Location();
            patch.location.displayName = location;
        }
        if (details != null && !details.isBlank()) {
            patch.body = new ItemBody();
            patch.body.contentType = BodyType.TEXT;
            patch.body.content = details;
        }
        if (dateFrom != null) {
            patch.start = EventDetailsTransformer.toGraphDateTime(dateFrom);
        }
        if (dateTo != null) {
            patch.end = EventDetailsTransformer.toGraphDateTime(dateTo);
        }
        GraphServiceClient<okhttp3.Request> graphClient = client(ctx, invokerId);
        return graphClient.users(email).events(eventId).buildRequest().patch(patch);
    }

    private static Attendee buildAttendee(String email, AttendeeType type) {
        Attendee attendee = new Attendee();
        attendee.emailAddress = new EmailAddress();
        attendee.emailAddress.address = email;
        attendee.type = type;
        return attendee;
    }

    private static void requireEventId(String eventId) {
        if (eventId == null || eventId.isBlank()) {
            throw new IllegalArgumentException("Event Id is required.");
        }
    }

    /** Actions (accept/decline/tentativelyAccept/cancel) return no content — re-fetch to return the
     * updated Event Details. Swallows a re-fetch failure (the action itself already succeeded). */
    private static Event getEventQuietly(GraphServiceClient<okhttp3.Request> graphClient, String email, String eventId) {
        try {
            return graphClient.users(email).events(eventId).buildRequest().get();
        } catch (Exception e) {
            return null;
        }
    }
}
