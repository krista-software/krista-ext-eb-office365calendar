package app.krista.extensions.essentials.collaboration.eboffice365calendar.integration;

import app.krista.extension.authorization.MustAuthorizeException;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.auth.EBOffice365CalendarAttributeStore;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.auth.EBOffice365CalendarAuditService;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.auth.EBOffice365CalendarMsAuthAttributes;
import app.krista.extensions.util.KeyValueStore;
import app.krista.ksdk.context.AuthorizationContext;
import app.krista.ksdk.context.RequestContext;
import app.krista.sdk.msauth.extension.service.MsAuthGraphClientProvider;
import com.microsoft.graph.requests.GraphServiceClient;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

/**
 * Thin alias over the SDK's MsAuthGraphClientProvider. Single-mailbox-per-invoker extension — the
 * operator path is fully implemented; forRequest() dispatches on RequestContext#invokeAsUser() (Trap
 * 0.12) but there is no per-user consent flow implemented (not requested — MCP is out of scope).
 *
 * MsAuthGraphClientProvider is NEVER @Inject-ed directly (see Trap 0.16) — it is constructed manually
 * per call from attrs freshly loaded from the AttributeStore.
 */
@Service
public class EBOffice365CalendarGraphClientProvider {

    private final KeyValueStore keyValueStore;
    private final EBOffice365CalendarAttributeStore attributeStore;
    private final RequestContext requestContext;
    private final AuthorizationContext authorizationContext;
    private final EBOffice365CalendarAuditService auditService;

    @Inject
    public EBOffice365CalendarGraphClientProvider(KeyValueStore keyValueStore, EBOffice365CalendarAttributeStore attributeStore,
                                                   RequestContext requestContext, AuthorizationContext authorizationContext,
                                                   EBOffice365CalendarAuditService auditService) {
        this.keyValueStore = keyValueStore;
        this.attributeStore = attributeStore;
        this.requestContext = requestContext;
        this.authorizationContext = authorizationContext;
        this.auditService = auditService;
    }

    /** Operator/admin context — the single connection configured via the Authentication tab. */
    public GraphServiceClient<okhttp3.Request> forOperator(String invokerId) throws IOException {
        EBOffice365CalendarMsAuthAttributes attrs = attributeStore.load(invokerId);
        if (attrs == null) {
            throw new MustAuthorizeException(
                    "Not configured — complete the Authentication tab first.", List.of());
        }
        MsAuthGraphClientProvider provider = new MsAuthGraphClientProvider(
                keyValueStore, attrs, requestContext, authorizationContext, invokerId);
        provider.setAuditService(auditService);
        // useSetupEmail=true — NEVER false/null here (see Trap 0.9): a single-mailbox extension always
        // resolves the configured attrs.getEmail(), never AuthorizationContext#getAuthorizedAccount()
        // (only populated during a real end-user's live catalog request).
        return provider.getGraphServiceClientForUser(true, null);
    }

    /** Every real catalog request routes through here, dispatching on invokeAsUser() — see Trap 0.12. */
    public GraphServiceClient<okhttp3.Request> forRequest(RequestContext requestContext, String invokerId)
            throws IOException {
        if (!requestContext.invokeAsUser()) {
            return forOperator(invokerId);
        }
        throw new MustAuthorizeException(
                "Sign-in on behalf of this user isn't supported by this extension — it's connected once, "
                        + "for the whole team, via the Authentication tab.", List.of());
    }

    public String getEmail(String invokerId) {
        EBOffice365CalendarMsAuthAttributes attrs = attributeStore.load(invokerId);
        return attrs != null ? attrs.getEmail() : null;
    }
}
