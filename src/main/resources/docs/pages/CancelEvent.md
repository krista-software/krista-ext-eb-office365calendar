# Cancel Event

## Overview
Cancel an event on the connected mailbox's calendar, notifying attendees automatically.

## Request Details
- **Area**: Meetings
- **Type**: CHANGE_SYSTEM
- **External API**: `POST /users/{email}/events/{eventId}/cancel`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Event Id | Text | Yes | The Microsoft Graph event identifier. |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Event Details | Entity(Event Details) | The cancelled event's last-known state, re-fetched after the action. May be absent if the event is no longer retrievable. See [Entities](pages/entities.md). |

## Example
**Input**: `Event Id="AAMkAGI2..."`
**Output**: `Event Details={Event Id: "AAMkAGI2...", Title: "Planning Sync", ...}`

## Error Handling
- **Missing Event Id** — fails with a validation error before any Graph call.
- **Microsoft Graph errors** are translated into an actionable message.
