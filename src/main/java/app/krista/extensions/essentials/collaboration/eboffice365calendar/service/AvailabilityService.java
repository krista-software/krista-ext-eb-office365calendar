package app.krista.extensions.essentials.collaboration.eboffice365calendar.service;

import app.krista.extensions.essentials.collaboration.eboffice365calendar.impl.transformers.EventDetailsTransformer;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.integration.EBOffice365CalendarGraphClientProvider;
import app.krista.ksdk.context.RequestContext;
import com.microsoft.graph.http.BaseCollectionPage;
import com.microsoft.graph.models.CalendarGetScheduleParameterSet;
import com.microsoft.graph.models.ScheduleInformation;
import com.microsoft.graph.requests.GraphServiceClient;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Orchestrates the Availability area's single Graph call: POST /users/{email}/calendar/getSchedule. */
@Service
public class AvailabilityService {

    private final EBOffice365CalendarGraphClientProvider graphClientProvider;

    @Inject
    public AvailabilityService(EBOffice365CalendarGraphClientProvider graphClientProvider) {
        this.graphClientProvider = graphClientProvider;
    }

    public List<ScheduleInformation> getFreeBusySchedule(RequestContext ctx, String invokerId, String email,
                                                          List<String> schedules, Long startDateTime, Long endDateTime,
                                                          Integer availabilityViewInterval) throws IOException {
        if (schedules == null || schedules.isEmpty()) {
            throw new IllegalArgumentException("Email Addresses is required.");
        }
        if (startDateTime == null || endDateTime == null) {
            throw new IllegalArgumentException("Start Date Time and End Date Time are required.");
        }
        if (endDateTime < startDateTime) {
            throw new IllegalArgumentException("End Date Time must be after Start Date Time.");
        }
        GraphServiceClient<okhttp3.Request> graphClient = graphClientProvider.forRequest(ctx, invokerId);
        CalendarGetScheduleParameterSet.CalendarGetScheduleParameterSetBuilder builder = CalendarGetScheduleParameterSet.newBuilder()
                .withSchedules(schedules)
                .withStartTime(EventDetailsTransformer.toGraphDateTime(startDateTime))
                .withEndTime(EventDetailsTransformer.toGraphDateTime(endDateTime));
        if (availabilityViewInterval != null) {
            builder = builder.withAvailabilityViewInterval(availabilityViewInterval);
        }
        BaseCollectionPage page = graphClient.users(email).calendar()
                .getSchedule(builder.build())
                .buildRequest(Collections.emptyList())
                .post();
        List<ScheduleInformation> result = new ArrayList<>();
        if (page != null) {
            for (Object o : page.getCurrentPage()) {
                if (o instanceof ScheduleInformation) {
                    result.add((ScheduleInformation) o);
                }
            }
        }
        return result;
    }
}
