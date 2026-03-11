package com.example.campuseventplatform.controller;

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
    public Participation registerStudent(
            @RequestParam Long studentId,
            @RequestParam Long eventId) {

        return participationService.registerStudent(studentId, eventId);
    }
}