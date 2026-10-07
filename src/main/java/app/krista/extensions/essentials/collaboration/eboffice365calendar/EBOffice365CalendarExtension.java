package app.krista.extensions.essentials.collaboration.eboffice365calendar;

import app.krista.extension.executor.Invoker;
import app.krista.extension.impl.anno.ChangeLog;
import app.krista.extension.impl.anno.Containerize;
import app.krista.extension.impl.anno.Extension;
import app.krista.extension.impl.anno.InvokerRequest;
import app.krista.extension.impl.anno.Java;
import app.krista.extension.impl.anno.StaticResource;
import app.krista.extension.authorization.RequestAuthenticator;
import app.krista.extension.request.RoutingInfo;
import app.krista.extension.request.protos.http.HttpProtocol;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.auth.EBOffice365CalendarAttributeStore;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.auth.EBOffice365CalendarTokenStore;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.integration.EBOffice365CalendarGraphClientProvider;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.integration.GraphErrorTranslator;
import app.krista.ksdk.context.AuthorizationContext;
import com.microsoft.graph.requests.GraphServiceClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;

/**
 * EB Office365Calendar extension entry point. Pattern D (Microsoft-auth-SDK) — connection fields live
 * entirely on the custom Authentication tab (EBOffice365CalendarAuthResource), not as @Field.* on this
 * class.
 */
@Extension(name = "EB Office365Calendar", version = "1.0.0",
        description = "Manage Office 365 calendars and events via Microsoft Graph, with Microsoft "
                + "single sign-on on the Setup page.",
        implementationModel = Extension.ImplementationModel.Krista_4_O)
@Containerize(baseImageVersion = "3.6.2-sp3")
@StaticResource(path = "docs", file = "docs")
@ChangeLog(file = "docs/pages/ReleaseNotes.md")
@Java(version = Java.Version.JAVA_21)
public final class EBOffice365CalendarExtension {

    private static final Logger LOGGER = LoggerFactory.getLogger(EBOffice365CalendarExtension.class);

    private final Invoker invoker;
    private final EBOffice365CalendarAttributeStore attributeStore;
    private final EBOffice365CalendarGraphClientProvider graphClientProvider;
    private final EBOffice365CalendarRequestAuthenticator requestAuthenticator;

    @Inject
    public EBOffice365CalendarExtension(Invoker invoker, EBOffice365CalendarAttributeStore attributeStore,
                                         EBOffice365CalendarTokenStore tokenStore,
                                         EBOffice365CalendarGraphClientProvider graphClientProvider,
                                         AuthorizationContext authorizationContext) {
        this.invoker = invoker;
        this.attributeStore = attributeStore;
        this.graphClientProvider = graphClientProvider;
        // HK2 cannot bind a bare String (invokerId/routingUrl) in ANY context — build the
        // RequestAuthenticator with plain `new` here, deriving the String values from the already-
        // injected Invoker instead. See Trap 0.6 in adding-krista-msauth-tabs/SKILL.md.
        String routingUrl = invoker.getRoutingInfo().getRoutingURL(HttpProtocol.PROTOCOL_NAME, RoutingInfo.Type.APPLIANCE);
        this.requestAuthenticator = new EBOffice365CalendarRequestAuthenticator(
                attributeStore, tokenStore, invoker.getInvokerId(), authorizationContext, routingUrl);
    }

    @InvokerRequest(InvokerRequest.Type.CUSTOM_TABS)
    public Map<String, String> customTabs() {
        return Map.of(
                "Authentication", "rest/eboffice365calendar/authentication/",
                "Documentation", "static/docs",
                "EB Office365Calendar Audit Dashboard", "rest/eboffice365calendar/audit/");
    }

    /**
     * Setup tab's own Test-Connection button — checks the ALREADY-SAVED attributes. Distinct from the
     * Authentication tab's own Test Connection (checks the candidate/not-yet-saved form data) — see
     * Trap 0.7; both must work independently.
     */
    @InvokerRequest(InvokerRequest.Type.TEST_CONNECTION)
    public void testConnection() throws Exception {
        String invokerId = invoker.getInvokerId();
        if (attributeStore.load(invokerId) == null) {
            throw new IllegalArgumentException(
                    "No credentials configured. Please authenticate via the Authentication tab first.");
        }
        try {
            String email = graphClientProvider.getEmail(invokerId);
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException(
                        "No email configured for this connection. Please reconfigure via the Authentication tab.");
            }
            GraphServiceClient<okhttp3.Request> client = graphClientProvider.forOperator(invokerId);
            client.users(email).events().buildRequest().top(1).get();
        } catch (Exception e) {
            LOGGER.error("Setup-tab Test Connection failed for invoker {}: {}", invokerId, e.getMessage(), e);
            throw new IllegalStateException(GraphErrorTranslator.toFriendlyMessage(e), e);
        }
    }

    @InvokerRequest(InvokerRequest.Type.AUTHENTICATOR)
    public RequestAuthenticator getRequestAuthenticator() {
        return requestAuthenticator;
    }
}
