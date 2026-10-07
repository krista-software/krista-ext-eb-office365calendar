# Get Calendar

## Overview
Fetch a single calendar by id — use this to confirm a calendar's name, color, and edit permission
before scheduling against it.

## Request Details
- **Area**: Calendars
- **Type**: QUERY_SYSTEM
- **External API**: `GET /users/{email}/calendars/{calendarId}`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Calendar Id | Text | Yes | The Microsoft Graph calendar identifier (from List Calendars). |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Calendar | Entity(Calendar) | The matching calendar. See [Entities](pages/entities.md). |

## Example
**Input**: `Calendar Id="AAMkAGI2..."`
**Output**: `Calendar={Id: "AAMkAGI2...", Name: "Calendar", Can Edit: true, ...}`

## Error Handling
- **Missing Calendar Id** — fails with a validation error before any Graph call is made.
- **Not found / no access** — Microsoft Graph errors are translated into an actionable message.
