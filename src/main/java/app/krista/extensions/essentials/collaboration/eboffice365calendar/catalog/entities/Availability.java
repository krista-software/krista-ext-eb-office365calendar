package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities;

import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Entity;
import app.krista.extension.impl.anno.Field;
import app.krista.extension.impl.anno.ToString;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * One free/busy time slot for a mailbox, as returned by Microsoft Graph's getSchedule. Net-new — no
 * equivalent existed in the Essentials/Collaboration domain catalog.
 */
@Domain(id = "catEntryDomain_5fa2fc97-4b17-44cf-b98f-aa91a459a091",
        name = "Collaboration",
        ecosystemId = "catEntryEcosystem_84b53163-327b-4b1b-8c96-9334d292f9f5",
        ecosystemName = "Essentials",
        ecosystemVersion = "f908fbe7-79d9-4f84-a28c-7d5ded1df6b4")
@Entity(name = "Availability",
        id = "localDomainEntity_c5e2a1f4-8d3b-4a6e-9f12-5b7d3c8a9e44",
        primaryKey = "Schedule Id",
        supportStore = false,
        description = "A free/busy time slot for a mailbox over a requested time range.")
public class Availability {

    @ToString
    @Field.Text(name = "Schedule Id", required = false, description = "The mailbox email this slot belongs to.")
    public String scheduleId;

    @Field.Text(name = "Status", required = false, description = "free, tentative, busy, oof, or workingElsewhere.")
    public String status;

    @Field.Date(name = "Start", required = false, includeTimeOfDay = true,
            allowPast = true, allowToday = true, allowFuture = true)
    public Long start;

    @Field.Date(name = "End", required = false, includeTimeOfDay = true,
            allowPast = true, allowToday = true, allowFuture = true)
    public Long end;

    @Field.Text(name = "Availability View", required = false,
            description = "Per-interval digit code for the whole requested range (0=free 1=tentative 2=busy 3=oof 4=workingElsewhere).")
    public String availabilityView;

    public Map<String, Object> toFields() {
        Map<String, Object> fieldMap = new LinkedHashMap<>();
        fieldMap.put("Schedule Id", scheduleId);
        fieldMap.put("Status", status);
        fieldMap.put("Start", start);
        fieldMap.put("End", end);
        fieldMap.put("Availability View", availabilityView);
        return fieldMap;
    }
}
