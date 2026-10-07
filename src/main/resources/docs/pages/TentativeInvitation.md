# Tentative Invitation

## Overview
Tentatively accept a meeting invitation on behalf of the connected mailbox.

## Request Details
- **Area**: Meetings
- **Type**: CHANGE_SYSTEM
- **External API**: `POST /users/{email}/events/{eventId}/tentativelyAccept`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Event Id | Text | Yes | The Microsoft Graph event identifier. |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Event Details | Entity(Event Details) | The event, re-fetched after responding. See [Entities](pages/entities.md). |

## Example
**Input**: `Event Id="AAMkAGI2..."`
**Output**: `Event Details={Event Id: "AAMkAGI2...", Title: "Planning Sync", ...}`

## Error Handling
- **Missing Event Id** — fails with a validation error before any Graph call.
- **Microsoft Graph errors** are translated into an actionable message.
