package com.niet.eventportal.model;

public class Event {
    private int id;
    private String name;
    private String description;
    private String eventDate; // yyyy-MM-dd
    private String venue;
    private String imageUrl;
    private String tableName; // dedicated registration table for this event

    public Event() {}

    public Event(int id, String name, String description, String eventDate,
                 String venue, String imageUrl, String tableName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.eventDate = eventDate;
        this.venue = venue;
        this.imageUrl = imageUrl;
        this.tableName = tableName;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEventDate() { return eventDate; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
}
