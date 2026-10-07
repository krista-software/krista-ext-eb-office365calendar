package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog;

import app.krista.extension.executor.ExtensionResponse;
import app.krista.extension.executor.ExtensionResponseBuilder;
import app.krista.extension.executor.Invoker;
import app.krista.extension.impl.anno.CatalogRequest;
import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Field;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.integration.EBOffice365CalendarGraphClientProvider;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.integration.GraphErrorTranslator;
import com.microsoft.graph.requests.GraphServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.LinkedHashMap;
import java.util.Map;

/** Standard Setup area — workflow-callable Test Connection + Health Check, matching the builder's
 * non-negotiable (independent of the Authentication tab's own Test Connection and of the Setup tab's
 * own @InvokerRequest(TEST_CONNECTION) hook — see Trap 0.7). */
@Domain(id = "catEntryDomain_5fa2fc97-4b17-44cf-b98f-aa91a459a091",
        name = "Collaboration",
        ecosystemId = "catEntryEcosystem_84b53163-327b-4b1b-8c96-9334d292f9f5",
        ecosystemName = "Essentials",
        ecosystemVersion = "f908fbe7-79d9-4f84-a28c-7d5ded1df6b4")
public class SetupArea {

    private static final Logger LOG = LoggerFactory.getLogger(SetupArea.class);

    private final EBOffice365CalendarGraphClientProvider graphClientProvider;
    private final Invoker invoker;

    @Inject
    public SetupArea(EBOffice365CalendarGraphClientProvider graphClientProvider, Invoker invoker) {
        this.graphClientProvider = graphClientProvider;
        this.invoker = invoker;
    }

    @CatalogRequest(
            id = "localDomainRequest_6f3a1d9e-5c2b-4a7e-8d4f-1b9e3c6a2f57",
            name = "Test Connection",
            description = "Validate connectivity and calendar access for the connected mailbox.",
            area = "Setup",
            type = CatalogRequest.Type.QUERY_SYSTEM,
            tool = true)
    @Field.Boolean(name = "Success", required = false)
    @Field.Text(name = "Message", required = false)
    public ExtensionResponse testConnection() {
        String invokerId = invoker.getInvokerId();
        Map<String, Object> out = new LinkedHashMap<>();
        try {
            String email = graphClientProvider.getEmail(invokerId);
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Not configured. Please authenticate via the Authentication tab first.");
            }
            GraphServiceClient<okhttp3.Request> client = graphClientProvider.forOperator(invokerId);
            client.users(email).events().buildRequest().top(1).get();
            out.put("Success", true);
            out.put("Message", "Connected to " + email + ".");
        } catch (Exception e) {
            LOG.error("Test connection failed for invoker {}: {}", invokerId, e.getMessage(), e);
            out.put("Success", false);
            out.put("Message", GraphErrorTranslator.toFriendlyMessage(e));
        }
        return new ExtensionResponseBuilder().success(out).build();
    }

    @CatalogRequest(
            id = "localDomainRequest_4d9e2a6c-7f1b-4e3a-9c8d-5b2e6f3a1d94",
            name = "Health Check",
            description = "Check extension and calendar-connectivity health.",
            area = "Setup",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Boolean(name = "Is Healthy", required = false)
    @Field(name = "Heap Used MB", type = "Number", required = false)
    @Field.Text(name = "Details", required = false)
    public ExtensionResponse healthCheck() {
        String invokerId = invoker.getInvokerId();
        Map<String, Object> out = new LinkedHashMap<>();
        MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
        long heapUsedMb = memBean.getHeapMemoryUsage().getUsed() / (1024 * 1024);
        boolean healthy;
        String details;
        try {
            String email = graphClientProvider.getEmail(invokerId);
            if (email == null || email.isBlank()) {
                healthy = false;
                details = "Not configured.";
            } else {
                GraphServiceClient<okhttp3.Request> client = graphClientProvider.forOperator(invokerId);
                client.users(email).events().buildRequest().top(1).get();
                healthy = true;
                details = "OK";
            }
        } catch (Exception e) {
            LOG.error("Health check failed for invoker {}: {}", invokerId, e.getMessage(), e);
            healthy = false;
            details = GraphErrorTranslator.toFriendlyMessage(e);
        }
        out.put("Is Healthy", healthy);
        out.put("Heap Used MB", (double) heapUsedMb);
        out.put("Details", details);
        return new ExtensionResponseBuilder().success(out).build();
    }
}
