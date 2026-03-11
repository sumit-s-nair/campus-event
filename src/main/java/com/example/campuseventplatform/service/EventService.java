package com.example.campuseventplatform.service;

import com.example.campuseventplatform.model.Event;
import com.example.campuseventplatform.model.EventStatus;
import com.example.campuseventplatform.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event createEvent(Event event) {
        event.setStatus(EventStatus.DRAFT);
        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    public Event submitEvent(Long id) {
        Event event = eventRepository.findById(id).orElseThrow();
        event.setStatus(EventStatus.SUBMITTED);
        return eventRepository.save(event);
    }

    public Event facultyApprove(Long id) {
        Event event = eventRepository.findById(id).orElseThrow();
        event.setStatus(EventStatus.FACULTY_APPROVED);
        return eventRepository.save(event);
    }

    public Event adminApprove(Long id) {
        Event event = eventRepository.findById(id).orElseThrow();
        event.setStatus(EventStatus.ADMIN_APPROVED);
        return eventRepository.save(event);
    }

    public Event publishEvent(Long id) {
        Event event = eventRepository.findById(id).orElseThrow();
        event.setStatus(EventStatus.PUBLISHED);
        return eventRepository.save(event);
    }
}