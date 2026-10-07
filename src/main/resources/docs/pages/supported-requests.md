# Supported Requests

EB Office365Calendar exposes **17 catalog requests** across **4 areas**. Each links to its detailed page.

## Calendars
| Request | Type | Description |
|---|---|---|
| [List Calendars](pages/ListCalendars.md) | QUERY_SYSTEM | List every calendar available to the connected mailbox. |
| [Get Calendar](pages/GetCalendar.md) | QUERY_SYSTEM | Fetch a single calendar by id. |
| [Create Calendar](pages/CreateCalendar.md) | CHANGE_SYSTEM | Create a new secondary calendar. |

## Meetings
| Request | Type | Description |
|---|---|---|
| [Schedule Meeting](pages/ScheduleMeeting.md) | CHANGE_SYSTEM | Schedule a new meeting/event. |
| [Cancel Event](pages/CancelEvent.md) | CHANGE_SYSTEM | Cancel an event you organize. |
| [Accept Invitation](pages/AcceptInvitation.md) | CHANGE_SYSTEM | Accept a meeting invitation. |
| [Decline Invitation](pages/DeclineInvitation.md) | CHANGE_SYSTEM | Decline a meeting invitation. |
| [Tentative Invitation](pages/TentativeInvitation.md) | CHANGE_SYSTEM | Tentatively accept a meeting invitation. |
| [Add Attendees](pages/AddAttendees.md) | CHANGE_SYSTEM | Add an attendee to an existing event. |
| [Remove Attendee](pages/RemoveAttendee.md) | CHANGE_SYSTEM | Remove an attendee from an existing event. |
| [Get Next Upcoming Meetings](pages/GetNextUpcomingMeetings.md) | CHANGE_SYSTEM | Fetch the next N upcoming meetings. |
| [Get All Unaccepted Events](pages/GetAllUnacceptedEvents.md) | CHANGE_SYSTEM | List events with no RSVP response yet. |
| [Get Calendar View](pages/GetCalendarView.md) | QUERY_SYSTEM | List events on a calendar within a date/time range. |
| [Update Event](pages/UpdateEvent.md) | CHANGE_SYSTEM | Update an existing event's subject, location, details, or time. |

## Availability
| Request | Type | Description |
|---|---|---|
| [Get Free/Busy Schedule](pages/GetFreeBusySchedule.md) | QUERY_SYSTEM | Check free/busy availability for one or more mailboxes. |

## Setup
| Request | Type | Description |
|---|---|---|
| [Test Connection](pages/TestConnection.md) | QUERY_SYSTEM | Validate connectivity and calendar access. |
| [Health Check](pages/HealthCheck.md) | CHANGE_SYSTEM | Check extension and calendar-connectivity health. |
