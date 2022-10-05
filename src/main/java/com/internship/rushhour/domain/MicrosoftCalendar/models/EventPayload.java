package com.internship.rushhour.domain.MicrosoftCalendar.models;

import com.microsoft.graph.models.Attendee;
import com.microsoft.graph.models.DateTimeTimeZone;
import com.microsoft.graph.models.ItemBody;

import java.util.List;

public class EventPayload {
    private String subject;
    private ItemBody body;
    private DateTimeTimeZone start;
    private DateTimeTimeZone end;
    private List<Attendee> attendees;

    public EventPayload(){}

    public EventPayload(String subject, ItemBody body, DateTimeTimeZone start, DateTimeTimeZone end, List<Attendee> attendees) {
        this.subject = subject;
        this.body = body;
        this.start = start;
        this.end = end;
        this.attendees = attendees;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public ItemBody getBody() {
        return body;
    }

    public void setBody(ItemBody body) {
        this.body = body;
    }

    public DateTimeTimeZone getStart() {
        return start;
    }

    public void setStart(DateTimeTimeZone start) {
        this.start = start;
    }

    public DateTimeTimeZone getEnd() {
        return end;
    }

    public void setEnd(DateTimeTimeZone end) {
        this.end = end;
    }

    public List<Attendee> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<Attendee> attendees) {
        this.attendees = attendees;
    }
}
