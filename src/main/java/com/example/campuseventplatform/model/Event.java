package com.example.campuseventplatform.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @ManyToOne
    private User organizer;

    public Event(){}

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public void transitionTo(EventStatus newStatus, User userActivatingChange) {
        if (this.status == newStatus) {
            throw new IllegalStateException("Event is already in state: " + newStatus);
        }

        if (userActivatingChange == null || userActivatingChange.getRole() == null) {
            throw new IllegalArgumentException("Requesting user is required for status transitions");
        }

        if (!userActivatingChange.getRole().canAuthorize(newStatus)) {
            throw new SecurityException("User does not have permission to change event to " + newStatus);
        }

        this.status = newStatus;
    }

    public Participation createParticipation(User student) {
        Participation participation = new Participation();
        participation.setStudent(student);
        participation.setEvent(this);
        return participation;
    }

    public Sponsorship createSponsorship(User sponsor, Double amount) {
        Sponsorship sponsorship = new Sponsorship();
        sponsorship.setSponsor(sponsor);
        sponsorship.setEvent(this);
        sponsorship.setAmount(amount);
        return sponsorship;
    }
}