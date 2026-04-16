package com.example.campuseventplatform.dto;

import java.time.LocalDate;

public class EventCreateDTO {
    private String title;
    private String description;
    private LocalDate date;
    private Long organizerId;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Long getOrganizerId() { return organizerId; }
    public void setOrganizerId(Long organizerId) { this.organizerId = organizerId; }
}
