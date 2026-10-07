# Get Next Upcoming Meetings

## Overview
Fetch a number of upcoming meetings based on provided input, ordered soonest-first.

## Request Details
- **Area**: Meetings
- **Type**: CHANGE_SYSTEM
- **External API**: `GET /users/{email}/events?$filter=start/dateTime ge {now}&$orderby=start/dateTime&$top={n}`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Number of meeting | Number | Yes | How many upcoming meetings to return (must be greater than zero). |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Event Details | [ Entity(Event Details) ] | The next upcoming meetings, soonest first. See [Entities](pages/entities.md). |

## Example
**Input**: `Number of meeting=5`
**Output**: `Event Details=[{Title: "Planning Sync", ...}, ...]` (up to 5 items)

## Error Handling
- **Number of meeting is zero or negative** — fails with a validation error before any Graph call.
- **Microsoft Graph errors** are translated into an actionable message.
