# Get Free/Busy Schedule

## Overview
Check free/busy availability for one or more mailboxes over a time range — use this before scheduling
a meeting to find a time that works for everyone.

## Request Details
- **Area**: Availability
- **Type**: QUERY_SYSTEM
- **External API**: `POST /users/{email}/calendar/getSchedule`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Email Addresses | [ Text ] | Yes | The mailboxes to check. |
| Start Date Time | Date | Yes | Range start. |
| End Date Time | Date | Yes | Range end; must be after Start Date Time. |
| Availability View Interval | Number | No | Minutes per slot in the returned Availability View digit code. |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Availability | [ Entity(Availability) ] | One row per busy/tentative/out-of-office slot found. A mailbox with no slots in range is fully free and contributes no rows. See [Entities](pages/entities.md). |

## Example
**Input**: `Email Addresses=["jane@example.com"]`, `Start Date Time=2026-01-15T09:00`, `End Date Time=2026-01-15T17:00`
**Output**: `Availability=[{Schedule Id: "jane@example.com", Status: "BUSY", Start: ..., End: ...}]`

## Error Handling
- **Empty Email Addresses, missing dates, or End before Start** — fails with a validation error before
  any Graph call.
- **Microsoft Graph errors** are translated into an actionable message.
