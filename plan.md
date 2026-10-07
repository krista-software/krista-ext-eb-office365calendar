# EB Office365Calendar — Implementation Plan

## Summary
A Krista Global Catalog extension integrating Office 365 / Microsoft 365 calendars via the
Microsoft Graph API (`https://graph.microsoft.com/v1.0`). Covers the basic calendar lifecycle —
managing calendars, scheduling and updating meetings, responding to invitations, managing
attendees, browsing a date range, and checking free/busy availability — with Microsoft
single sign-on wired into the Setup/Authentication tab.

- **Extension name (slug):** `eb-office365calendar`
- **Title:** EB Office365Calendar
- **Version:** 1.0.0
- **Package:** `app.krista.extensions.essentials.collaboration.eboffice365calendar`
- **Ecosystem / Domain:** Essentials / Collaboration

## Authentication — Pattern D (Microsoft)
Microsoft products always use Krista's `microsoft-auth-sdk` pattern (non-negotiable), regardless
of the raw OAuth category answer. The confirmed answer (`oauth2-client-credentials,oauth2-interactive`)
maps onto the SDK's two supported `AuthFlow`s, both exposed as admin-selectable options on the
**same** Authentication tab (per `adding-krista-msauth-tabs`'s Trap 0 — one tab, multiple flows):

| Mode | Flow | How the admin sets it up |
|---|---|---|
| **User Authentication** (primary — delivers the requested SSO) | `AUTH_CODE_PUBLIC`/`AUTH_CODE_PRIVATE` (delegated, interactive) | Admin clicks "Sign in with Microsoft" on the Setup page (Quick Setup, using Krista's shared multi-tenant app) or supplies their own Azure AD app's client id/secret for the same interactive sign-in (Your Own Credentials) |
| **Server to Server** | `CLIENT_CREDENTIALS_SECRET` (application, no user sign-in) | Admin supplies a client id/secret/tenant for an app-only connection — no interactive popup |

Single connected mailbox per invoker (operator model) — no per-user delegation, since nothing in
the requirement describes multiple end users authenticating with their own Microsoft identity.
Required Graph permission: **Calendars.ReadWrite** (Delegated for User Authentication, Application
for Server to Server — these are two different permission grants in Azure AD, documented
separately per Trap 0.15).

## Areas

### Calendars
| Request | Inputs | Output | External endpoint | Reused / New |
|---|---|---|---|---|
| List Calendars | — | `[ Entity(Calendar) ]` | `GET /me/calendars` | New |
| Get Calendar | Calendar Id | `Entity(Calendar)` | `GET /me/calendars/{calendarId}` | New |
| Create Calendar | Name | `Entity(Calendar)` | `POST /me/calendars` | New |

### Meetings
| Request | Inputs | Output | External endpoint | Reused / New |
|---|---|---|---|---|
| Schedule Meeting | Title, Location, Date From, Date to, Details, Attendee Email | `Entity(Event Details)` | `POST /me/calendars/{calendarId}/events` | **Reused** — `localDomainRequest_f9cf27ee` |
| Cancel Event | Event Id | `Entity(Event Details)` | `POST /me/events/{eventId}/cancel` | **Reused** — `localDomainRequest_44d8fe1f` |
| Accept Invitation | Event Id | `Entity(Event Details)` | `POST /me/events/{eventId}/accept` | **Reused** — `localDomainRequest_d564897b` |
| Decline Invitation | Event Id | `Entity(Event Details)` | `POST /me/events/{eventId}/decline` | **Reused** — `localDomainRequest_9de59f5f` |
| Tentative Invitation | Event Id | `Entity(Event Details)` | `POST /me/events/{eventId}/tentativelyAccept` | **Reused** — `localDomainRequest_5bf7458f` |
| Add Attendees | Event Id, Attendee Email | `Entity(Event Details)` | `PATCH /me/events/{eventId}` | **Reused** — `localDomainRequest_2e73b843` |
| Remove Attendee | Event Id, Attendee Email | `Entity(Event Details)` | `PATCH /me/events/{eventId}` | **Reused** — `localDomainRequest_653a694e` |
| Get Next Upcoming Meetings | Number of meeting | `[ Entity(Event Details) ]` | `GET /me/events?$filter=start/dateTime ge {now}&$orderby=start/dateTime&$top={n}` | **Reused** — `localDomainRequest_771024d0` |
| Get All Unaccepted Events | — | `[ Entity(Event Details) ]` | `GET /me/events?$filter=responseStatus/response eq 'notResponded'` | **Reused** — `localDomainRequest_e0f748a2` |
| Get Calendar View | Calendar Id (optional), Start Date Time, End Date Time | `[ Entity(Event Details) ]` | `GET /me/calendars/{calendarId}/calendarView?startDateTime=…&endDateTime=…` | New |
| Update Event | Event Id, Title, Location, Details, Date From, Date to (all but Event Id optional) | `Entity(Event Details)` | `PATCH /me/events/{eventId}` | New |

### Availability
| Request | Inputs | Output | External endpoint | Reused / New |
|---|---|---|---|---|
| Get Free/Busy Schedule | Email Addresses, Start Date Time, End Date Time, Availability View Interval (optional) | `[ Entity(Availability) ]` | `POST /me/calendar/getSchedule` | New |

## Entities
| Entity | Fields | Reused / New |
|---|---|---|
| Event Details | Event Id (Text), Location (Text), Title (Text), Contacts (Checkboxes), Details (Paragraph), Date From (Date), Date To (Date) | **Reused** — `localDomainEntity_b13aad88` |
| Attendee | Response Status (Text), Id (Text), Display Name (Text), Comment (Paragraph), Email (Email) | **Reused** — `localDomainEntity_3774533e` |
| Calendar | Id (Identifier), Name (Text), Color (Text), Can Edit (Switch), Owner (Text) | New |
| Availability | Schedule Id (Text), Status (Text), Start (Date), End (Date), Availability View (Text) | New |

## Capabilities
| Capability | Included? |
|---|---|
| Entity search | **No** — explicitly excluded (see Domain reuse below) |
| Webhooks / WAIT_FOR_EVENT | No — not requested |
| MCP (`tool = true`) | No — not requested |
| Solutions tab | No — not requested |
| File handling | No — no attachment/binary-file operation requested |

## Domain reuse
Essentials/Collaboration already has a production Outlook/Meetings integration with substantial
overlap. Per your per-item review:

- **Reused verbatim** (same title/id/field shape, no duplicate invented): entities *Event Details*
  and *Attendee*; requests *Schedule Meeting*, *Cancel Event*, *Accept Invitation*,
  *Decline Invitation*, *Tentative Invitation*, *Add Attendees*, *Remove Attendee*,
  *Get Next Upcoming Meetings*, *Get All Unaccepted Events*.
- **Excluded from scope (avoided)**: the four existing entity-search infrastructure requests
  (*Fetch Fields Supporting Search For Event Details*, *Entity Selector For Event Details*,
  *Fetch Fields Supporting Search ForAttendee*, *Entity Selector For Attendee*) — this build does
  **not** implement entity search for either entity.
- **Genuinely new** (no existing match): *List Calendars*, *Get Calendar*, *Create Calendar*,
  *Get Calendar View*, *Update Event*, *Get Free/Busy Schedule*, and the *Calendar* /
  *Availability* entities backing them.

## Placeholder IDs — must replace before deploy
- `@Domain` `domainId` / `ecosystemId` for Essentials/Collaboration are **platform-registered,
  shared values** — no sibling extension checkout for this specific domain was available in this
  workspace to copy them from. Stable placeholder UUIDs will be minted so the code compiles, but
  they **must** be swapped for the real registered Essentials/Collaboration IDs (matching the
  existing Outlook integration's own `@Domain` block byte-for-byte) before this extension can bind
  or deploy.
