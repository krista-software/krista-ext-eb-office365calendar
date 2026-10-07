# Entities

Data structures returned by the requests. Request output tables link here instead of repeating the
full field list.

## Event Details
A calendar event/meeting, with its time, location, and attendees. Primary key: **Event Id**. Reused
verbatim (same shape) from the existing Essentials/Collaboration Outlook integration.

| Field | Type | Description |
|---|---|---|
| Event Id | Text | The unique Microsoft Graph event identifier. |
| Title | Text | The event subject/title. |
| Location | Text | The display name of the event location. |
| Contacts | Checkboxes | Attendees, formatted as "Display Name \<email\>". |
| Details | Paragraph | The event body/description. |
| Date From | Date | Event start date/time. |
| Date To | Date | Event end date/time. |

## Attendee
A meeting attendee and their RSVP status. Primary key: **Id** (the attendee's email). Reused verbatim
from the existing Essentials/Collaboration domain; used internally to build Event Details' Contacts
field (an entity's own fields can never nest another entity).

| Field | Type | Description |
|---|---|---|
| Id | Text | Stable identifier (the attendee's email). |
| Display Name | Text | The attendee's display name. |
| Email | Email | The attendee's email address. |
| Response Status | Text | none, accepted, declined, tentativelyAccepted, or organizer. |
| Comment | Paragraph | An optional RSVP comment. |

## Calendar
A calendar container that holds events. Primary key: **Id**. Net-new, generalized so a future
extension in this domain can reuse it.

| Field | Type | Description |
|---|---|---|
| Id | Identifier | The unique Microsoft Graph calendar identifier. |
| Name | Text | The calendar's display name. |
| Color | Text | The calendar's display color, e.g. AUTO or LIGHT_BLUE. |
| Can Edit | Switch | Whether the connected account can edit this calendar. |
| Owner | Text | The calendar owner's display name. |

## Availability
One free/busy time slot for a mailbox, as returned by Microsoft Graph's getSchedule. Primary key:
**Schedule Id** (not unique per row — a mailbox can have many slots). Net-new.

| Field | Type | Description |
|---|---|---|
| Schedule Id | Text | The mailbox email this slot belongs to. |
| Status | Text | FREE, TENTATIVE, BUSY, OOF, or WORKING_ELSEWHERE. |
| Start | Date | Slot start date/time. |
| End | Date | Slot end date/time. |
| Availability View | Text | Per-interval digit code for the whole requested range. |
