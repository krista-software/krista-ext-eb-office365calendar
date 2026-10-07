# Authentication

EB Office365Calendar authenticates with Microsoft Entra ID (Azure AD) via the **Authentication** tab,
built on Krista's microsoft-auth-sdk. Three sign-in modes are available, all on the same tab:

- **Quick Setup** (Krista-managed multi-tenant app, single sign-on) — **not available for this
  deployment**: Krista has not registered a multi-tenant Azure AD app for this extension yet. The
  Quick Setup option is hidden; use **Your Own Credentials** below instead.
- **Your Own Credentials** (interactive, delegated sign-in with your own Azure AD app) — a specific
  user signs in with their Microsoft account.
- **Server to Server** (client credentials, application/app-only) — no interactive sign-in; the
  extension calls Graph as the app itself.

## Obtaining credentials (Your Own Credentials / Server to Server)

1. In the [Azure Portal](https://learn.microsoft.com/en-us/entra/identity-platform/quickstart-register-app),
   register a new App Registration (or reuse an existing one).
2. Add the Graph API permission **Calendars.ReadWrite** — as a **Delegated** permission for interactive
   sign-in, or an **Application** permission (with admin consent) for Server to Server.
3. Create a client secret under "Certificates & secrets" and copy its **Value** (not its Secret ID).
4. On the Authentication tab, choose the matching mode and enter the Client ID, Client Secret, and
   Tenant ID (and, for interactive sign-in, the mailbox Email).

## Required scopes / permissions

| Mode | Required Graph permission | Permission type | Admin consent required? |
|---|---|---|---|
| **Your Own Credentials** (delegated — a specific user signs in) | Calendars.ReadWrite | Delegated | Only if your tenant restricts user consent |
| **Server to Server** (client credentials — no user sign-in) | Calendars.ReadWrite | **Application** | Always yes |

Grant only the permission type matching the mode you actually use — a Delegated grant does nothing for
Server to Server (client credentials) mode, and vice versa; Azure AD evaluates these as two completely
separate permission sets.

## Verifying

Click **Test Connection** on the Authentication tab (checks the form's current, not-yet-saved values),
or run the Setup area's [Test Connection](pages/TestConnection.md) request (checks the already-saved
connection). Both make one real call — listing the first event on the connected mailbox's calendar —
so a success means the actual Calendars.ReadWrite permission genuinely works, not just that a token was
issued.
