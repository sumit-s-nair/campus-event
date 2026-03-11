package com.example.campuseventplatform.controller;

import com.example.campuseventplatform.model.Event;
import com.example.campuseventplatform.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public Event createEvent(@RequestBody Event event) {
        return eventService.createEvent(event);
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    @PatchMapping("/{id}/submit")
    public Event submitEvent(@PathVariable Long id) {
        return eventService.submitEvent(id);
    }

    @PatchMapping("/{id}/faculty-approve")
    public Event facultyApprove(@PathVariable Long id) {
        return eventService.facultyApprove(id);
    }

    @PatchMapping("/{id}/admin-approve")
    public Event adminApprove(@PathVariable Long id) {
        return eventService.adminApprove(id);
    }

    @PatchMapping("/{id}/publish")
    public Event publishEvent(@PathVariable Long id) {
        return eventService.publishEvent(id);
    }
}