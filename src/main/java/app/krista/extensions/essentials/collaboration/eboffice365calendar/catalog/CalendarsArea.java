package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog;

import app.krista.extension.authorization.MustAuthorizeException;
import app.krista.extension.executor.ExtensionResponse;
import app.krista.extension.executor.ExtensionResponseBuilder;
import app.krista.extension.executor.Invoker;
import app.krista.extension.impl.anno.Attribute;
import app.krista.extension.impl.anno.CatalogRequest;
import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Field;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities.Calendar;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.errorhandlers.AuthorizationExceptionHandler;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.impl.transformers.CalendarTransformer;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.integration.EBOffice365CalendarGraphClientProvider;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.integration.GraphErrorTranslator;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.service.CalendarService;
import app.krista.ksdk.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Calendar-container management: list/get/create the calendars available to the connected mailbox.
 */
@Domain(id = "catEntryDomain_5fa2fc97-4b17-44cf-b98f-aa91a459a091",
        name = "Collaboration",
        ecosystemId = "catEntryEcosystem_84b53163-327b-4b1b-8c96-9334d292f9f5",
        ecosystemName = "Essentials",
        ecosystemVersion = "f908fbe7-79d9-4f84-a28c-7d5ded1df6b4")
public class CalendarsArea {

    private static final Logger LOG = LoggerFactory.getLogger(CalendarsArea.class);

    private final CalendarService calendarService;
    private final EBOffice365CalendarGraphClientProvider graphClientProvider;
    private final Invoker invoker;
    private final RequestContext requestContext;

    @Inject
    public CalendarsArea(CalendarService calendarService, EBOffice365CalendarGraphClientProvider graphClientProvider,
                          Invoker invoker, RequestContext requestContext) {
        this.calendarService = calendarService;
        this.graphClientProvider = graphClientProvider;
        this.invoker = invoker;
        this.requestContext = requestContext;
    }

    @CatalogRequest(
            id = "localDomainRequest_8d2e4a6c-1f3b-4c9e-a7d5-2b6f9e3c1a84",
            name = "List Calendars",
            description = "List every calendar available to the connected mailbox.",
            area = "Calendars",
            type = CatalogRequest.Type.QUERY_SYSTEM,
            tool = true)
    @Field.Desc(name = "Calendars", type = "[ Entity(Calendar) ]", required = false)
    @Field(name = "Count", type = "Number", required = false, attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public ExtensionResponse listCalendars() {
        String invokerId = invoker.getInvokerId();
        try {
            String email = graphClientProvider.getEmail(invokerId);
            List<com.microsoft.graph.models.Calendar> calendars = calendarService.listCalendars(requestContext, invokerId, email);
            List<Calendar> entities = CalendarTransformer.transformList(calendars);
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("Calendars", entities);
            out.put("Count", (double) entities.size());
            return new ExtensionResponseBuilder().success(out).build();
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (IllegalArgumentException e) {
            return new ExtensionResponseBuilder().failure(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("List Calendars failed for invoker {}: {}", invokerId, e.getMessage(), e);
            return new ExtensionResponseBuilder().failure(GraphErrorTranslator.toFriendlyMessage(e)).build();
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_5a3c7e9f-2d4b-4e6a-8c1f-9b3d6a2e4f75",
            name = "Get Calendar",
            description = "Fetch a single calendar by id.",
            area = "Calendars",
            type = CatalogRequest.Type.QUERY_SYSTEM,
            tool = true)
    @Field.Desc(name = "Calendar", type = "Entity(Calendar)", required = false)
    public ExtensionResponse getCalendar(
            @Field.Text(name = "Calendar Id", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String calendarId) {
        String invokerId = invoker.getInvokerId();
        try {
            String email = graphClientProvider.getEmail(invokerId);
            Calendar entity = CalendarTransformer.transform(calendarService.getCalendar(requestContext, invokerId, email, calendarId));
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("Calendar", entity);
            return new ExtensionResponseBuilder().success(out).build();
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (IllegalArgumentException e) {
            return new ExtensionResponseBuilder().failure(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Get Calendar failed for invoker {}: {}", invokerId, e.getMessage(), e);
            return new ExtensionResponseBuilder().failure(GraphErrorTranslator.toFriendlyMessage(e)).build();
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_9c1e3a5d-6f2b-4d8e-a4c7-3e9b2d5f6a18",
            name = "Create Calendar",
            description = "Create a new secondary calendar for the connected mailbox.",
            area = "Calendars",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Desc(name = "Calendar", type = "Entity(Calendar)", required = false)
    public ExtensionResponse createCalendar(
            @Field.Text(name = "Name", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String name) {
        String invokerId = invoker.getInvokerId();
        try {
            String email = graphClientProvider.getEmail(invokerId);
            Calendar entity = CalendarTransformer.transform(calendarService.createCalendar(requestContext, invokerId, email, name));
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("Calendar", entity);
            return new ExtensionResponseBuilder().success(out).build();
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (IllegalArgumentException e) {
            return new ExtensionResponseBuilder().failure(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Create Calendar failed for invoker {}: {}", invokerId, e.getMessage(), e);
            return new ExtensionResponseBuilder().failure(GraphErrorTranslator.toFriendlyMessage(e)).build();
        }
    }
}
