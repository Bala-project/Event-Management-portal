package com.niet.eventportal.model;

public class Registration {
    private int id;
    private int eventId;      // used only when submitting a new registration
    private String eventName; // populated when listing a student's registered events
    private String name;
    private String email;
    private String phone;
    private String department;
    private String year;
    private String registeredAt;

    public Registration() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(String registeredAt) { this.registeredAt = registeredAt; }
}
