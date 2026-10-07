# List Calendars

## Overview
List every calendar available to the connected mailbox — use this to discover calendar ids before
calling Get Calendar, Schedule Meeting, or Get Calendar View against a non-default calendar.

## Request Details
- **Area**: Calendars
- **Type**: QUERY_SYSTEM
- **External API**: `GET /users/{email}/calendars`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
None.

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Calendars | [ Entity(Calendar) ] | Every calendar on the connected mailbox. See [Entities](pages/entities.md). |
| Count | Number | Number of calendars returned. |

## Example
**Input**: (none)
**Output**: `Calendars=[{Id: "AAMk...", Name: "Calendar", ...}, {Id: "AAMk...", Name: "Team Events", ...}]`, `Count=2`

## Error Handling
- **Not configured** — if the Authentication tab has no saved connection, the request fails with an
  authentication error pointing at the Authentication tab.
- **Microsoft Graph errors** (invalid/expired credentials, insufficient permissions) are translated into
  an actionable message rather than a raw SDK error.
