package com.example.campuseventplatform.service;

import com.example.campuseventplatform.model.Sponsorship;
import com.example.campuseventplatform.repository.SponsorshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SponsorshipService {

    private final SponsorshipRepository sponsorshipRepository;

    public SponsorshipService(SponsorshipRepository sponsorshipRepository) {
        this.sponsorshipRepository = sponsorshipRepository;
    }

    public Sponsorship sponsorEvent(Sponsorship sponsorship) {
        return sponsorshipRepository.save(sponsorship);
    }
}