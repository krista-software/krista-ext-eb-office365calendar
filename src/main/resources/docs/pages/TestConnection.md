# Test Connection

## Overview
Validate connectivity and calendar access for the connected mailbox — a workflow-callable equivalent
of the Setup tab's own Test Connection button, checking the already-saved connection.

## Request Details
- **Area**: Setup
- **Type**: QUERY_SYSTEM
- **External API**: `GET /users/{email}/events?$top=1`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
None.

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Success | Boolean | Whether the connectivity check succeeded. |
| Message | Text | A human-readable result or actionable error message. |

## Example
**Input**: (none)
**Output**: `Success=true`, `Message="Connected to jane@example.com."`

## Error Handling
- **Not configured** — returns `Success=false` with a message pointing at the Authentication tab.
- **Microsoft Graph errors** are translated into an actionable message rather than a raw SDK error.
