package com.example.campuseventplatform.service;

import com.example.campuseventplatform.model.Event;
import com.example.campuseventplatform.model.Participation;
import com.example.campuseventplatform.model.User;
import com.example.campuseventplatform.repository.EventRepository;
import com.example.campuseventplatform.repository.ParticipationRepository;
import com.example.campuseventplatform.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public ParticipationService(
            ParticipationRepository participationRepository,
            UserRepository userRepository,
            EventRepository eventRepository) {

        this.participationRepository = participationRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    public Participation registerStudent(Long studentId, Long eventId) {

        User student = userRepository.findById(studentId).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();

        Participation participation = event.createParticipation(student);

        return participationRepository.save(participation);
    }
}