package com.example.campuseventplatform.model;

import jakarta.persistence.*;

@Entity
public class Sponsorship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    @ManyToOne
    private User sponsor;

    @ManyToOne
    private Event event;

    public Sponsorship(){}

    public Long getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public User getSponsor() {
        return sponsor;
    }

    public void setSponsor(User sponsor) {
        this.sponsor = sponsor;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}