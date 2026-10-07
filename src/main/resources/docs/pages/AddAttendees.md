# Add Attendees

## Overview
Add an attendee to an existing event, notifying the event organizer's update to attendees.

## Request Details
- **Area**: Meetings
- **Type**: CHANGE_SYSTEM
- **External API**: `GET` then `PATCH /users/{email}/events/{eventId}` (reads the current attendee list, appends, and saves)
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Event Id | Text | Yes | The Microsoft Graph event identifier. |
| Attendee Email | Email | Yes | The attendee to add. |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Event Details | Entity(Event Details) | The updated event. See [Entities](pages/entities.md). |

## Example
**Input**: `Event Id="AAMkAGI2..."`, `Attendee Email="sam@example.com"`
**Output**: `Event Details={Event Id: "AAMkAGI2...", Contacts: ["Sam Lee <sam@example.com>"], ...}`

## Error Handling
- **Missing Event Id or Attendee Email** — fails with a validation error before any Graph call.
- **Attendee already present** — the request is idempotent; it will not add a duplicate entry.
- **Microsoft Graph errors** are translated into an actionable message.
