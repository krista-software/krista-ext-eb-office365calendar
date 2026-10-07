package app.krista.extensions.essentials.collaboration.eboffice365calendar.auth;

import org.jvnet.hk2.annotations.ContractsProvided;
import org.jvnet.hk2.annotations.Service;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Set;

/**
 * Registers EBOffice365CalendarAuthResource (Authentication tab, Pattern A) and
 * EBOffice365CalendarAuditResource (Audit tab) under one JAX-RS application. "eboffice365calendar" is
 * the segment right after "rest/" in every tab/callback URL this extension exposes — it must stay
 * consistent with customTabs() and every routingUrl-based URL built in AuthResource.
 */
@Service
@ApplicationPath("eboffice365calendar")
@ContractsProvided(Application.class)
public class EBOffice365CalendarApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(EBOffice365CalendarAuthResource.class, EBOffice365CalendarAuditResource.class);
    }
}
