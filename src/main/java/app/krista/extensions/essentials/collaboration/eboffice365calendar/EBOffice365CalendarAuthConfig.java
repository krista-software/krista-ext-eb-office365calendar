package app.krista.extensions.essentials.collaboration.eboffice365calendar;

import app.krista.sdk.msauth.config.AuthFlow;
import app.krista.sdk.msauth.extension.annotations.AuthScope;
import app.krista.sdk.msauth.extension.annotations.AuditConfig;
import app.krista.sdk.msauth.extension.annotations.MsAuthExtension;

/**
 * Microsoft-auth-SDK declarative config (Pattern D). Intentionally empty — all auth/scope/audit
 * configuration is expressed as annotations. Registered via EBOffice365CalendarExtensionDescriptor.
 */
@MsAuthExtension(
        extensionType = "eboffice365calendar",
        callbackPathPrefix = "/eboffice365calendar",
        supportedFlows = {AuthFlow.AUTH_CODE_PUBLIC, AuthFlow.AUTH_CODE_PRIVATE, AuthFlow.CLIENT_CREDENTIALS_SECRET},
        defaultScopes = {"openid", "offline_access", "Calendars.ReadWrite"},
        headerTitle = "EB Office365Calendar Authentication",
        headerSubtitle = "Configure how Krista connects to your Office 365 calendar",
        showScopePanel = true,
        supportsOwnCredentials = true,
        enableAuditDashboard = true,
        setupGuideUrl = "https://learn.microsoft.com/en-us/entra/identity-platform/quickstart-register-app"
)
@AuthScope(value = "Calendars.ReadWrite",
        description = "View, create, update, and delete events and calendars in the connected mailbox",
        required = true,
        catalogRequests = {"Test Connection", "Health Check", "List Calendars", "Get Calendar", "Create Calendar",
                "Schedule Meeting", "Cancel Event", "Accept Invitation", "Decline Invitation", "Tentative Invitation",
                "Add Attendees", "Remove Attendee", "Get Next Upcoming Meetings", "Get All Unaccepted Events",
                "Get Calendar View", "Update Event", "Get Free/Busy Schedule"})
@AuditConfig(
        dashboardTitle = "EB Office365Calendar Audit Dashboard",
        enableCredentialHealth = true,
        enableCharts = true,
        csvExportPrefix = "eboffice365calendar-audit"
)
public class EBOffice365CalendarAuthConfig {
}
