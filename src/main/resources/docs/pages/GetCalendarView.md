# Get Calendar View

## Overview
List events on a calendar within a date/time range — the general-purpose way to browse a calendar
(including recurring-event expansion), unlike the narrower Get Next Upcoming Meetings / Get All
Unaccepted Events requests.

## Request Details
- **Area**: Meetings
- **Type**: QUERY_SYSTEM
- **External API**: `GET /users/{email}/calendars/{calendarId}/calendarView?startDateTime=...&endDateTime=...`
  (default calendar when no Calendar Id is given)
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Calendar Id | Text | No | Defaults to the mailbox's default calendar when omitted. |
| Start Date Time | Date | Yes | Range start. |
| End Date Time | Date | Yes | Range end; must be after Start Date Time. |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Event Details | [ Entity(Event Details) ] | Events within the requested range. See [Entities](pages/entities.md). |

## Example
**Input**: `Start Date Time=2026-01-01T00:00`, `End Date Time=2026-01-31T23:59`
**Output**: `Event Details=[{Title: "Planning Sync", ...}, ...]`

## Error Handling
- **Missing Start/End Date Time, or End before Start** — fails with a validation error before any Graph call.
- **Microsoft Graph errors** are translated into an actionable message.
