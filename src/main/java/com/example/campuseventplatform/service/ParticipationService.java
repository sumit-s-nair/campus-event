package com.example.campuseventplatform.service;

import com.example.campuseventplatform.model.Participation;
import com.example.campuseventplatform.repository.ParticipationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParticipationService {

    private final ParticipationRepository participationRepository;

    public ParticipationService(ParticipationRepository participationRepository) {
        this.participationRepository = participationRepository;
    }

    public Participation register(Participation participation) {
        return participationRepository.save(participation);
    }
}