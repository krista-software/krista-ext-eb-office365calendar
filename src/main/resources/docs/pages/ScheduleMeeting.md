# Schedule Meeting

## Overview
Schedule a new meeting/event on the connected mailbox's calendar, optionally inviting one attendee.

## Request Details
- **Area**: Meetings
- **Type**: CHANGE_SYSTEM
- **External API**: `POST /users/{email}/calendars/{calendarId}/events` (default calendar when no
  Calendar Id is given)
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Title | Text | Yes | The event subject. |
| Location | Text | No | The event location's display name. |
| Date From | Date | Yes | Event start date/time. |
| Date to | Date | Yes | Event end date/time. |
| Details | Paragraph | No | The event body/description. |
| Attendee Email | Email | No | A single attendee to invite. |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Event Details | Entity(Event Details) | The created event. See [Entities](pages/entities.md). |

## Example
**Input**: `Title="Planning Sync"`, `Date From=2026-01-15T09:00`, `Date to=2026-01-15T10:00`, `Attendee Email="jane@example.com"`
**Output**: `Event Details={Event Id: "AAMk...", Title: "Planning Sync", ...}`

## Error Handling
- **Missing Title, Date From, or Date to** — fails with a validation error before any Graph call.
- **Microsoft Graph errors** are translated into an actionable message.

## Related Requests
Only one attendee can be invited here — use [Add Attendees](pages/AddAttendees.md) afterward to invite
more.
