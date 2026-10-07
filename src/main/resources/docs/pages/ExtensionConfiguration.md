# Configuration

This extension uses a custom **Authentication** tab instead of standard Setup-tab connection fields —
see [Authentication](pages/Authentication.md) for how to connect it.

## Setup tab

The plain Setup tab has no connection fields of its own for this extension. Its **Test Connection**
button (and the workflow-callable [Test Connection](pages/TestConnection.md) /
[Health Check](pages/HealthCheck.md) requests in the Setup area) check the credentials already saved
via the Authentication tab — configure the connection there first.

## Notes

- Configuration is stored per-invoker — each invoker keeps its own independent connection.
- A connection serves one mailbox (the account signed in or configured on the Authentication tab); all
  requests act against that single mailbox.
