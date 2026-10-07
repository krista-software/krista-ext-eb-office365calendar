# Remove Attendee

## Overview
Remove an attendee from an existing event.

## Request Details
- **Area**: Meetings
- **Type**: CHANGE_SYSTEM
- **External API**: `GET` then `PATCH /users/{email}/events/{eventId}` (reads the current attendee list, removes the match, and saves)
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Event Id | Text | Yes | The Microsoft Graph event identifier. |
| Attendee Email | Email | Yes | The attendee to remove. |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Event Details | Entity(Event Details) | The updated event. See [Entities](pages/entities.md). |

## Example
**Input**: `Event Id="AAMkAGI2..."`, `Attendee Email="sam@example.com"`
**Output**: `Event Details={Event Id: "AAMkAGI2...", Contacts: [], ...}`

## Error Handling
- **Missing Event Id or Attendee Email** — fails with a validation error before any Graph call.
- **Attendee not present** — no-op; the event is saved unchanged.
- **Microsoft Graph errors** are translated into an actionable message.
