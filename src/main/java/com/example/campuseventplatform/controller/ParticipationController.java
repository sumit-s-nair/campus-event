package com.example.campuseventplatform.controller;

import com.example.campuseventplatform.dto.ParticipationResponseDTO;
import com.example.campuseventplatform.model.Participation;
import com.example.campuseventplatform.service.ParticipationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/participation")
public class ParticipationController {

    private final ParticipationService participationService;

    public ParticipationController(ParticipationService participationService) {
        this.participationService = participationService;
    }

    @PostMapping("/register")
    public ParticipationResponseDTO registerStudent(
            @RequestParam Long studentId,
            @RequestParam Long eventId) {

        Participation participation = participationService.registerStudent(studentId, eventId);
        return mapToDTO(participation);
    }

    private ParticipationResponseDTO mapToDTO(Participation participation) {
        ParticipationResponseDTO dto = new ParticipationResponseDTO();
        dto.setParticipationId(participation.getId());

        if (participation.getEvent() != null) {
            dto.setEventId(participation.getEvent().getId());
            dto.setEventTitle(participation.getEvent().getTitle());
        }

        if (participation.getStudent() != null) {
            dto.setStudentName(participation.getStudent().getName());
        }

        return dto;
    }
}