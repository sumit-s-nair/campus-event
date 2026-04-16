package com.example.campuseventplatform.service;

import com.example.campuseventplatform.dto.EventCreateDTO;
import com.example.campuseventplatform.dto.StatusUpdateDTO;
import com.example.campuseventplatform.model.Event;
import com.example.campuseventplatform.model.EventStatus;
import com.example.campuseventplatform.model.User;
import com.example.campuseventplatform.repository.EventRepository;
import com.example.campuseventplatform.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Event createEvent(EventCreateDTO eventDto) {
        Event event = new Event();
        event.setTitle(eventDto.getTitle());
        event.setDescription(eventDto.getDescription());
        event.setDate(eventDto.getDate());
        event.setStatus(EventStatus.DRAFT);

        if (eventDto.getOrganizerId() != null) {
            User organizer = userRepository.findById(eventDto.getOrganizerId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Organizer ID"));
            event.setOrganizer(organizer);
        }

        return eventRepository.save(event);
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    @Override
    public Event updateEventStatus(Long id, StatusUpdateDTO statusUpdate) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        if (statusUpdate.getRequestingUserId() == null) {
            throw new IllegalArgumentException("Requesting user ID is required");
        }

        User user = userRepository.findById(statusUpdate.getRequestingUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        event.transitionTo(statusUpdate.getStatus(), user);

        return eventRepository.save(event);
    }
}
