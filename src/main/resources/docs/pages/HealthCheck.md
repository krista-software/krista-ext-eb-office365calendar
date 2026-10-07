# Health Check

## Overview
Check extension and calendar-connectivity health, including JVM heap usage — useful for scheduled
monitoring workflows.

## Request Details
- **Area**: Setup
- **Type**: CHANGE_SYSTEM
- **External API**: `GET /users/{email}/events?$top=1`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
None.

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Is Healthy | Boolean | Whether the extension can currently reach the connected mailbox's calendar. |
| Heap Used MB | Number | Current JVM heap usage, in megabytes. |
| Details | Text | "OK", "Not configured.", or a translated Microsoft Graph error. |

## Example
**Input**: (none)
**Output**: `Is Healthy=true`, `Heap Used MB=128.0`, `Details="OK"`

## Error Handling
- **Not configured** — returns `Is Healthy=false` with `Details="Not configured."`.
- **Microsoft Graph errors** are translated into an actionable message.
