# Create Calendar

## Overview
Create a new secondary calendar for the connected mailbox — use this to set up a dedicated calendar
(e.g. "Team Events") separate from the mailbox's default calendar.

## Request Details
- **Area**: Calendars
- **Type**: CHANGE_SYSTEM
- **External API**: `POST /users/{email}/calendars`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Name | Text | Yes | The new calendar's display name. |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Calendar | Entity(Calendar) | The newly created calendar. See [Entities](pages/entities.md). |

## Example
**Input**: `Name="Team Events"`
**Output**: `Calendar={Id: "AAMkAGI2...", Name: "Team Events", Can Edit: true, ...}`

## Error Handling
- **Missing/blank Name** — fails with a validation error before any Graph call is made.
- **Microsoft Graph errors** are translated into an actionable message.
