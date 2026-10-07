# Get All Unaccepted Events

## Overview
List events which are neither accepted nor rejected yet — useful to surface a "needs your response"
queue.

## Request Details
- **Area**: Meetings
- **Type**: CHANGE_SYSTEM
- **External API**: `GET /users/{email}/events?$filter=responseStatus/response eq 'notResponded'`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
None.

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Event Details | [ Entity(Event Details) ] | Events with no RSVP response yet. See [Entities](pages/entities.md). |

## Example
**Input**: (none)
**Output**: `Event Details=[{Title: "Budget Review", ...}]`

## Error Handling
- **Microsoft Graph errors** are translated into an actionable message.
