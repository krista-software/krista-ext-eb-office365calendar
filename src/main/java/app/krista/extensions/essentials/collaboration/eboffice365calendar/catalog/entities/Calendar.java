package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities;

import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Entity;
import app.krista.extension.impl.anno.Field;
import app.krista.extension.impl.anno.ToString;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A calendar container (e.g. a mailbox's default calendar, or a shared/secondary calendar). Net-new —
 * no equivalent existed in the Essentials/Collaboration domain catalog. Named generically ("Calendar",
 * not "Office365 Calendar") so a future extension in this domain can reuse it.
 */
@Domain(id = "catEntryDomain_5fa2fc97-4b17-44cf-b98f-aa91a459a091",
        name = "Collaboration",
        ecosystemId = "catEntryEcosystem_84b53163-327b-4b1b-8c96-9334d292f9f5",
        ecosystemName = "Essentials",
        ecosystemVersion = "f908fbe7-79d9-4f84-a28c-7d5ded1df6b4")
@Entity(name = "Calendar",
        id = "localDomainEntity_7a1e9c3d-4b6f-4e21-9d58-2c6b1a9f3e02",
        primaryKey = "Id",
        supportStore = false,
        description = "A calendar container that holds events.")
public class Calendar {

    @Field.Text(name = "Id", required = false, description = "The unique Microsoft Graph calendar identifier.")
    public String id;

    @ToString
    @Field.Text(name = "Name", required = false)
    public String name;

    @Field.Text(name = "Color", required = false, description = "The calendar's display color, e.g. 'auto' or 'lightBlue'.")
    public String color;

    @Field.Boolean(name = "Can Edit", required = false, description = "Whether the connected account can edit this calendar.")
    public Boolean canEdit;

    @Field.Text(name = "Owner", required = false, description = "The calendar owner's display name.")
    public String owner;

    public Map<String, Object> toFields() {
        Map<String, Object> fieldMap = new LinkedHashMap<>();
        fieldMap.put("Id", id);
        fieldMap.put("Name", name);
        fieldMap.put("Color", color);
        fieldMap.put("Can Edit", canEdit);
        fieldMap.put("Owner", owner);
        return fieldMap;
    }
}
