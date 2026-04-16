package com.example.campuseventplatform.controller;

import com.example.campuseventplatform.dto.EventCreateDTO;
import com.example.campuseventplatform.dto.EventResponseDTO;
import com.example.campuseventplatform.dto.StatusUpdateDTO;
import com.example.campuseventplatform.model.Event;
import com.example.campuseventplatform.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public EventResponseDTO createEvent(@RequestBody EventCreateDTO eventDTO) {
        Event createdEvent = eventService.createEvent(eventDTO);
        return mapToResponseDTO(createdEvent);
    }

    @GetMapping
    public List<EventResponseDTO> getAllEvents() {
        return eventService.getAllEvents().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public EventResponseDTO getEventById(@PathVariable Long id) {
        Event event = eventService.getEventById(id);
        return event != null ? mapToResponseDTO(event) : null;
    }

    @PatchMapping("/{id}/status")
    public EventResponseDTO updateStatus(@PathVariable Long id, @RequestBody StatusUpdateDTO statusUpdateDTO) {
        Event updatedEvent = eventService.updateEventStatus(id, statusUpdateDTO);
        return mapToResponseDTO(updatedEvent);
    }

    private EventResponseDTO mapToResponseDTO(Event event) {
        EventResponseDTO dto = new EventResponseDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setDate(event.getDate());
        if (event.getStatus() != null) {
            dto.setStatus(event.getStatus().name());
        }
        if (event.getOrganizer() != null) {
            dto.setOrganizerName(event.getOrganizer().getName());
        }
        return dto;
    }
}
