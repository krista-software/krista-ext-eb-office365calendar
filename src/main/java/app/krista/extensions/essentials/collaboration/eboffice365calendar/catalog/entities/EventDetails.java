package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities;

import app.krista.extension.impl.anno.Attribute;
import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Entity;
import app.krista.extension.impl.anno.Field;
import app.krista.extension.impl.anno.ToString;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A calendar event/meeting. Reused verbatim (same id/title/field shape) from the existing Essentials/
 * Collaboration domain's "Event Details" entity — not a new vendor-specific entity.
 */
@Domain(id = "catEntryDomain_5fa2fc97-4b17-44cf-b98f-aa91a459a091",
        name = "Collaboration",
        ecosystemId = "catEntryEcosystem_84b53163-327b-4b1b-8c96-9334d292f9f5",
        ecosystemName = "Essentials",
        ecosystemVersion = "f908fbe7-79d9-4f84-a28c-7d5ded1df6b4")
@Entity(name = "Event Details",
        id = "localDomainEntity_b13aad88-786e-440a-8131-84351124a9cf",
        primaryKey = "Event Id",
        supportStore = false,
        description = "A calendar event/meeting, with its time, location, and attendees.")
public class EventDetails {

    @Field.Text(name = "Event Id", required = false, description = "The unique Microsoft Graph event identifier.")
    public String eventId;

    @ToString
    @Field.Text(name = "Title", required = false, description = "The event subject/title.")
    public String title;

    @Field.Text(name = "Location", required = false, description = "The display name of the event location.")
    public String location;

    @Field.Desc(name = "Contacts", type = "[ Text ]", required = false,
            description = "Attendees, formatted as \"Display Name <email>\".")
    public List<String> contacts;

    @Field(name = "Details", type = "Paragraph", required = false, description = "The event body/description.")
    public String details;

    @Field.Date(name = "Date From", required = false, includeTimeOfDay = true,
            allowPast = true, allowToday = true, allowFuture = true,
            description = "Event start date/time.",
            attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {})
    public Long dateFrom;

    @Field.Date(name = "Date To", required = false, includeTimeOfDay = true,
            allowPast = true, allowToday = true, allowFuture = true,
            description = "Event end date/time.",
            attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {})
    public Long dateTo;

    public Map<String, Object> toFields() {
        Map<String, Object> fieldMap = new LinkedHashMap<>();
        fieldMap.put("Event Id", eventId);
        fieldMap.put("Title", title);
        fieldMap.put("Location", location);
        fieldMap.put("Contacts", contacts);
        fieldMap.put("Details", details);
        fieldMap.put("Date From", dateFrom);
        fieldMap.put("Date To", dateTo);
        return fieldMap;
    }
}
