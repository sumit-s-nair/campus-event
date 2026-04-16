package com.example.campuseventplatform.service;

import com.example.campuseventplatform.dto.EventCreateDTO;
import com.example.campuseventplatform.dto.StatusUpdateDTO;
import com.example.campuseventplatform.model.Event;

import java.util.List;

public interface EventService {

    Event createEvent(EventCreateDTO eventDto);

    List<Event> getAllEvents();

    Event getEventById(Long id);

    Event updateEventStatus(Long id, StatusUpdateDTO statusUpdate);
}