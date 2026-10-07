# Release Notes

- **Developer Name** : Krista Extension Builder
- **Krista Service APIs Java** : 1.0.125-sp1
- **Global Catalog Version** : Not Available

## Version 1.0.0 (2026)

Initial release.

### New Features
* Calendar management: List/Get/Create Calendar.
* Meeting lifecycle: Schedule, Update, Cancel, Accept/Decline/Tentatively-accept, Add/Remove Attendee.
* Browsing: Get Next Upcoming Meetings, Get All Unaccepted Events, Get Calendar View.
* Free/busy availability lookup across one or more mailboxes.
* Microsoft single sign-on (and Server-to-Server client credentials) on the Authentication tab.

### Resolved Bugs
* Not Available

### Dependency Updates
* Not Available

### Known Issues
* Not Available

### Limitations and Caveats
* Requires the Calendars.ReadWrite Graph permission (Delegated for interactive sign-in, Application for
  Server to Server).
* Quick Setup (Krista-managed multi-tenant sign-in) is not available until Krista registers a
  multi-tenant Azure AD app for this extension — use Your Own Credentials until then.
* One connection serves one mailbox; there is no per-end-user delegation model.
