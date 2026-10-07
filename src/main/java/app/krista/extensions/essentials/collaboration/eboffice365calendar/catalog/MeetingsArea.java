package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog;

import app.krista.extension.authorization.MustAuthorizeException;
import app.krista.extension.executor.ExtensionResponse;
import app.krista.extension.executor.ExtensionResponseBuilder;
import app.krista.extension.executor.Invoker;
import app.krista.extension.impl.anno.Attribute;
import app.krista.extension.impl.anno.CatalogRequest;
import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Field;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities.EventDetails;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.errorhandlers.AuthorizationExceptionHandler;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.impl.transformers.EventDetailsTransformer;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.integration.EBOffice365CalendarGraphClientProvider;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.integration.GraphErrorTranslator;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.service.MeetingService;
import app.krista.ksdk.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Meeting/event lifecycle: schedule, cancel, respond, manage attendees, and browse events. Nine of
 * these eleven requests are modeled verbatim (same title/id/field shape) on existing, already-
 * published Essentials/Collaboration catalog entries, per the approved domain-reuse review.
 */
@Domain(id = "catEntryDomain_5fa2fc97-4b17-44cf-b98f-aa91a459a091",
        name = "Collaboration",
        ecosystemId = "catEntryEcosystem_84b53163-327b-4b1b-8c96-9334d292f9f5",
        ecosystemName = "Essentials",
        ecosystemVersion = "f908fbe7-79d9-4f84-a28c-7d5ded1df6b4")
public class MeetingsArea {

    private static final Logger LOG = LoggerFactory.getLogger(MeetingsArea.class);

    private final MeetingService meetingService;
    private final EBOffice365CalendarGraphClientProvider graphClientProvider;
    private final Invoker invoker;
    private final RequestContext requestContext;

    @Inject
    public MeetingsArea(MeetingService meetingService, EBOffice365CalendarGraphClientProvider graphClientProvider,
                         Invoker invoker, RequestContext requestContext) {
        this.meetingService = meetingService;
        this.graphClientProvider = graphClientProvider;
        this.invoker = invoker;
        this.requestContext = requestContext;
    }

    private String email() {
        return graphClientProvider.getEmail(invoker.getInvokerId());
    }

    private ExtensionResponse eventResponse(EventDetails entity) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("Event Details", entity);
        return new ExtensionResponseBuilder().success(out).build();
    }

    private ExtensionResponse handle(String requestName, ThrowingSupplier<ExtensionResponse> body) {
        String invokerId = invoker.getInvokerId();
        try {
            return body.get();
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (IllegalArgumentException e) {
            return new ExtensionResponseBuilder().failure(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("{} failed for invoker {}: {}", requestName, invokerId, e.getMessage(), e);
            return new ExtensionResponseBuilder().failure(GraphErrorTranslator.toFriendlyMessage(e)).build();
        }
    }

    @FunctionalInterface
    private interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    @CatalogRequest(
            id = "localDomainRequest_f9cf27ee-ffe7-4b65-8e0a-ce0afa25f5dc",
            name = "Schedule Meeting",
            description = "Schedule a meeting invite and it will be scheduled on the next hour of that date",
            area = "Meetings",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Desc(name = "Event Details", type = "Entity(Event Details)", required = false)
    public ExtensionResponse scheduleMeeting(
            @Field.Text(name = "Title", required = true, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String title,
            @Field.Text(name = "Location", required = false, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String location,
            @Field.Date(name = "Date From", required = true, includeTimeOfDay = true, allowPast = false, allowToday = true, allowFuture = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) Long dateFrom,
            @Field.Date(name = "Date to", required = true, includeTimeOfDay = true, allowPast = false, allowToday = true, allowFuture = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) Long dateTo,
            @Field(name = "Details", type = "Paragraph", required = false, attributes = {@Attribute(name = "visualWidth", value = "L")}, options = {}) String details,
            @Field(name = "Attendee Email", type = "Email", required = false, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String attendeeEmail) {
        return handle("Schedule Meeting", () -> eventResponse(EventDetailsTransformer.transform(
                meetingService.scheduleMeeting(requestContext, invoker.getInvokerId(), email(), null,
                        title, location, dateFrom, dateTo, details, attendeeEmail))));
    }

    @CatalogRequest(
            id = "localDomainRequest_44d8fe1f-3f2d-438d-bb9f-bb486b9a9e56",
            name = "Cancel Event",
            description = "Cancel Event",
            area = "Meetings",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Desc(name = "Event Details", type = "Entity(Event Details)", required = false)
    public ExtensionResponse cancelEvent(
            @Field.Text(name = "Event Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String eventId) {
        return handle("Cancel Event", () -> eventResponse(EventDetailsTransformer.transform(
                meetingService.cancelEvent(requestContext, invoker.getInvokerId(), email(), eventId))));
    }

    @CatalogRequest(
            id = "localDomainRequest_d564897b-ad17-4289-af2c-bd79425c5341",
            name = "Accept Invitation",
            description = "Accept Invitation",
            area = "Meetings",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Desc(name = "Event Details", type = "Entity(Event Details)", required = false)
    public ExtensionResponse acceptInvitation(
            @Field.Text(name = "Event Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String eventId) {
        return handle("Accept Invitation", () -> eventResponse(EventDetailsTransformer.transform(
                meetingService.acceptInvitation(requestContext, invoker.getInvokerId(), email(), eventId))));
    }

    @CatalogRequest(
            id = "localDomainRequest_9de59f5f-b77d-42e3-84da-63f587f65358",
            name = "Decline Invitation",
            description = "Decline Invitation",
            area = "Meetings",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Desc(name = "Event Details", type = "Entity(Event Details)", required = false)
    public ExtensionResponse declineInvitation(
            @Field.Text(name = "Event Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String eventId) {
        return handle("Decline Invitation", () -> eventResponse(EventDetailsTransformer.transform(
                meetingService.declineInvitation(requestContext, invoker.getInvokerId(), email(), eventId))));
    }

    @CatalogRequest(
            id = "localDomainRequest_5bf7458f-953f-40c4-b75c-17b1f76a08b1",
            name = "Tentative Invitation",
            description = "Tentative Invitation",
            area = "Meetings",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Desc(name = "Event Details", type = "Entity(Event Details)", required = false)
    public ExtensionResponse tentativeInvitation(
            @Field.Text(name = "Event Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String eventId) {
        return handle("Tentative Invitation", () -> eventResponse(EventDetailsTransformer.transform(
                meetingService.tentativeInvitation(requestContext, invoker.getInvokerId(), email(), eventId))));
    }

    @CatalogRequest(
            id = "localDomainRequest_2e73b843-ace4-4b80-bfab-4f466706e5c5",
            name = "Add Attendees",
            description = "Add Attendees",
            area = "Meetings",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Desc(name = "Event Details", type = "Entity(Event Details)", required = false)
    public ExtensionResponse addAttendees(
            @Field.Text(name = "Event Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String eventId,
            @Field(name = "Attendee Email", type = "Email", required = true, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String attendeeEmail) {
        return handle("Add Attendees", () -> eventResponse(EventDetailsTransformer.transform(
                meetingService.addAttendees(requestContext, invoker.getInvokerId(), email(), eventId, attendeeEmail))));
    }

    @CatalogRequest(
            id = "localDomainRequest_653a694e-2327-41d5-8a8c-48b30b07dc89",
            name = "Remove Attendee",
            description = "Remove Attendee",
            area = "Meetings",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Desc(name = "Event Details", type = "Entity(Event Details)", required = false)
    public ExtensionResponse removeAttendee(
            @Field.Text(name = "Event Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String eventId,
            @Field(name = "Attendee Email", type = "Email", required = true, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String attendeeEmail) {
        return handle("Remove Attendee", () -> eventResponse(EventDetailsTransformer.transform(
                meetingService.removeAttendee(requestContext, invoker.getInvokerId(), email(), eventId, attendeeEmail))));
    }

    @CatalogRequest(
            id = "localDomainRequest_771024d0-02e5-4163-869e-8f8860a10b62",
            name = "Get Next Upcoming Meetings",
            description = "A Request to fetch number of upcoming meetings based on provided input",
            area = "Meetings",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Desc(name = "Event Details", type = "[ Entity(Event Details) ]", required = false)
    public ExtensionResponse getNextUpcomingMeetings(
            @Field(name = "Number of meeting", type = "Number", required = true, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) Double numberOfMeeting) {
        return handle("Get Next Upcoming Meetings", () -> {
            List<EventDetails> entities = EventDetailsTransformer.transformList(
                    meetingService.getNextUpcomingMeetings(requestContext, invoker.getInvokerId(), email(),
                            numberOfMeeting != null ? numberOfMeeting.intValue() : 0));
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("Event Details", entities);
            return new ExtensionResponseBuilder().success(out).build();
        });
    }

    @CatalogRequest(
            id = "localDomainRequest_e0f748a2-17df-4e29-84e0-a1d0d06934d9",
            name = "Get All Unaccepted Events",
            description = "Events which are neither accpted nor rejected",
            area = "Meetings",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Desc(name = "Event Details", type = "[ Entity(Event Details) ]", required = false)
    public ExtensionResponse getAllUnacceptedEvents() {
        return handle("Get All Unaccepted Events", () -> {
            List<EventDetails> entities = EventDetailsTransformer.transformList(
                    meetingService.getAllUnacceptedEvents(requestContext, invoker.getInvokerId(), email()));
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("Event Details", entities);
            return new ExtensionResponseBuilder().success(out).build();
        });
    }

    @CatalogRequest(
            id = "localDomainRequest_1b4f6a2e-9c3d-4e71-8a5f-6d2b9e1c4a73",
            name = "Get Calendar View",
            description = "List events on a calendar within a date/time range.",
            area = "Meetings",
            type = CatalogRequest.Type.QUERY_SYSTEM,
            tool = true)
    @Field.Desc(name = "Event Details", type = "[ Entity(Event Details) ]", required = false)
    public ExtensionResponse getCalendarView(
            @Field.Text(name = "Calendar Id", required = false, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String calendarId,
            @Field.Date(name = "Start Date Time", required = true, includeTimeOfDay = true, allowPast = true, allowToday = true, allowFuture = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) Long startDateTime,
            @Field.Date(name = "End Date Time", required = true, includeTimeOfDay = true, allowPast = true, allowToday = true, allowFuture = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) Long endDateTime) {
        return handle("Get Calendar View", () -> {
            List<EventDetails> entities = EventDetailsTransformer.transformList(
                    meetingService.getCalendarView(requestContext, invoker.getInvokerId(), email(), calendarId, startDateTime, endDateTime));
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("Event Details", entities);
            return new ExtensionResponseBuilder().success(out).build();
        });
    }

    @CatalogRequest(
            id = "localDomainRequest_3f9a2c7d-5e1b-4a68-9c3f-7a2d4e6b8f91",
            name = "Update Event",
            description = "Update an existing event's subject, location, details, or time.",
            area = "Meetings",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Desc(name = "Event Details", type = "Entity(Event Details)", required = false)
    public ExtensionResponse updateEvent(
            @Field.Text(name = "Event Id", required = true, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String eventId,
            @Field.Text(name = "Title", required = false, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String title,
            @Field.Text(name = "Location", required = false, attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String location,
            @Field(name = "Details", type = "Paragraph", required = false, attributes = {@Attribute(name = "visualWidth", value = "L")}, options = {}) String details,
            @Field.Date(name = "Date From", required = false, includeTimeOfDay = true, allowPast = false, allowToday = true, allowFuture = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) Long dateFrom,
            @Field.Date(name = "Date to", required = false, includeTimeOfDay = true, allowPast = false, allowToday = true, allowFuture = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) Long dateTo) {
        return handle("Update Event", () -> eventResponse(EventDetailsTransformer.transform(
                meetingService.updateEvent(requestContext, invoker.getInvokerId(), email(), eventId, title, location, details, dateFrom, dateTo))));
    }
}
