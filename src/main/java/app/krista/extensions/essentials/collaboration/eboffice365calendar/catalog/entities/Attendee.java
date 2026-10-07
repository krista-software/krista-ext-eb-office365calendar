package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities;

import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Entity;
import app.krista.extension.impl.anno.Field;
import app.krista.extension.impl.anno.ToString;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A meeting attendee. Reused verbatim (same id/title/field shape) from the existing Essentials/
 * Collaboration domain's "Attendee" entity. Used internally by EventDetailsTransformer to build the
 * Event Details entity's flattened "Contacts" list — see field-types.md: an @Entity's own fields can
 * never reference another @Entity, so Attendee is not nested directly inside Event Details.
 */
@Domain(id = "catEntryDomain_5fa2fc97-4b17-44cf-b98f-aa91a459a091",
        name = "Collaboration",
        ecosystemId = "catEntryEcosystem_84b53163-327b-4b1b-8c96-9334d292f9f5",
        ecosystemName = "Essentials",
        ecosystemVersion = "f908fbe7-79d9-4f84-a28c-7d5ded1df6b4")
@Entity(name = "Attendee",
        id = "localDomainEntity_3774533e-8dab-459d-9c39-5d2c262a7505",
        primaryKey = "Id",
        supportStore = false,
        description = "A meeting attendee and their RSVP status.")
public class Attendee {

    @Field.Text(name = "Id", required = false, description = "Stable identifier (the attendee's email).")
    public String id;

    @ToString
    @Field.Text(name = "Display Name", required = false)
    public String displayName;

    @Field(name = "Email", type = "Email", required = false)
    public String email;

    @Field.Text(name = "Response Status", required = false,
            description = "none, accepted, declined, tentativelyAccepted, or organizer.")
    public String responseStatus;

    @Field(name = "Comment", type = "Paragraph", required = false)
    public String comment;

    public Map<String, Object> toFields() {
        Map<String, Object> fieldMap = new LinkedHashMap<>();
        fieldMap.put("Id", id);
        fieldMap.put("Display Name", displayName);
        fieldMap.put("Email", email);
        fieldMap.put("Response Status", responseStatus);
        fieldMap.put("Comment", comment);
        return fieldMap;
    }
}
