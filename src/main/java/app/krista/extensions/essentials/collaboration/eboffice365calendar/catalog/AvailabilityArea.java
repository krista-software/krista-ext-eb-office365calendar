package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog;

import app.krista.extension.authorization.MustAuthorizeException;
import app.krista.extension.executor.ExtensionResponse;
import app.krista.extension.executor.ExtensionResponseBuilder;
import app.krista.extension.executor.Invoker;
import app.krista.extension.impl.anno.Attribute;
import app.krista.extension.impl.anno.CatalogRequest;
import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Field;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities.Availability;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.errorhandlers.AuthorizationExceptionHandler;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.impl.transformers.AvailabilityTransformer;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.integration.EBOffice365CalendarGraphClientProvider;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.integration.GraphErrorTranslator;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.service.AvailabilityService;
import app.krista.ksdk.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Free/busy availability lookups across one or more mailboxes. */
@Domain(id = "catEntryDomain_5fa2fc97-4b17-44cf-b98f-aa91a459a091",
        name = "Collaboration",
        ecosystemId = "catEntryEcosystem_84b53163-327b-4b1b-8c96-9334d292f9f5",
        ecosystemName = "Essentials",
        ecosystemVersion = "f908fbe7-79d9-4f84-a28c-7d5ded1df6b4")
public class AvailabilityArea {

    private static final Logger LOG = LoggerFactory.getLogger(AvailabilityArea.class);

    private final AvailabilityService availabilityService;
    private final EBOffice365CalendarGraphClientProvider graphClientProvider;
    private final Invoker invoker;
    private final RequestContext requestContext;

    @Inject
    public AvailabilityArea(AvailabilityService availabilityService, EBOffice365CalendarGraphClientProvider graphClientProvider,
                             Invoker invoker, RequestContext requestContext) {
        this.availabilityService = availabilityService;
        this.graphClientProvider = graphClientProvider;
        this.invoker = invoker;
        this.requestContext = requestContext;
    }

    @CatalogRequest(
            id = "localDomainRequest_2e6a9c3f-4d1b-4e8a-9c5f-7b3d2e6a9f40",
            name = "Get Free/Busy Schedule",
            description = "Check free/busy availability for one or more mailboxes over a time range.",
            area = "Availability",
            type = CatalogRequest.Type.QUERY_SYSTEM,
            tool = true)
    @Field.Desc(name = "Availability", type = "[ Entity(Availability) ]", required = false)
    public ExtensionResponse getFreeBusySchedule(
            @Field.Desc(name = "Email Addresses", type = "[ Text ]", required = true) List<String> emailAddresses,
            @Field.Date(name = "Start Date Time", required = true, includeTimeOfDay = true, allowPast = true, allowToday = true, allowFuture = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) Long startDateTime,
            @Field.Date(name = "End Date Time", required = true, includeTimeOfDay = true, allowPast = true, allowToday = true, allowFuture = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) Long endDateTime,
            @Field(name = "Availability View Interval", type = "Number", required = false,
                    attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) Double availabilityViewInterval) {
        String invokerId = invoker.getInvokerId();
        try {
            String email = graphClientProvider.getEmail(invokerId);
            List<Availability> entities = AvailabilityTransformer.transformList(
                    availabilityService.getFreeBusySchedule(requestContext, invokerId, email, emailAddresses,
                            startDateTime, endDateTime,
                            availabilityViewInterval != null ? availabilityViewInterval.intValue() : null));
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("Availability", entities);
            return new ExtensionResponseBuilder().success(out).build();
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (IllegalArgumentException e) {
            return new ExtensionResponseBuilder().failure(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Get Free/Busy Schedule failed for invoker {}: {}", invokerId, e.getMessage(), e);
            return new ExtensionResponseBuilder().failure(GraphErrorTranslator.toFriendlyMessage(e)).build();
        }
    }
}
