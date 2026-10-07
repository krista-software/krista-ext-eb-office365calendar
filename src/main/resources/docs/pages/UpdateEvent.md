# Update Event

## Overview
Update an existing event's subject, location, details, or time. Attendee changes are handled
separately — see [Add Attendees](pages/AddAttendees.md) / [Remove Attendee](pages/RemoveAttendee.md).

## Request Details
- **Area**: Meetings
- **Type**: CHANGE_SYSTEM
- **External API**: `PATCH /users/{email}/events/{eventId}`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Event Id | Text | Yes | The Microsoft Graph event identifier. |
| Title | Text | No | New subject; unchanged if omitted. |
| Location | Text | No | New location display name; unchanged if omitted. |
| Details | Paragraph | No | New body/description; unchanged if omitted. |
| Date From | Date | No | New start date/time; unchanged if omitted. |
| Date to | Date | No | New end date/time; unchanged if omitted. |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Event Details | Entity(Event Details) | The updated event. See [Entities](pages/entities.md). |

## Example
**Input**: `Event Id="AAMkAGI2..."`, `Location="Room 7"`
**Output**: `Event Details={Event Id: "AAMkAGI2...", Location: "Room 7", ...}`

## Error Handling
- **Missing Event Id** — fails with a validation error before any Graph call.
- **All other fields optional** — only the fields you provide are changed.
- **Microsoft Graph errors** are translated into an actionable message.
