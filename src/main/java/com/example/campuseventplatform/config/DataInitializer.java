package com.example.campuseventplatform.config;

import com.example.campuseventplatform.model.*;
import com.example.campuseventplatform.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRepository participationRepository;
    private final SponsorshipRepository sponsorshipRepository;

    public DataInitializer(UserRepository userRepository, EventRepository eventRepository,
                           ParticipationRepository participationRepository,
                           SponsorshipRepository sponsorshipRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.participationRepository = participationRepository;
        this.sponsorshipRepository = sponsorshipRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        // Users
        User admin = saveUser("Campus Admin", "admin@campus.edu", "admin", Role.ADMIN);
        User faculty = saveUser("Dr. Sharma", "faculty@campus.edu", "faculty", Role.FACULTY);
        User org1 = saveUser("Rahul Mehta", "organizer@campus.edu", "organizer", Role.ORGANIZER);
        User org2 = saveUser("Priya Nair", "coordinator@campus.edu", "coordinator", Role.ORGANIZER);
        User s1 = saveUser("Alice Fernandez", "alice@campus.edu", "student", Role.STUDENT);
        User s2 = saveUser("Bob Kumar", "bob@campus.edu", "student", Role.STUDENT);
        User s3 = saveUser("Carol Das", "carol@campus.edu", "student", Role.STUDENT);
        User sponsor = saveUser("TechCorp Ltd.", "sponsor@techcorp.com", "sponsor", Role.SPONSOR);

        // Events in various workflow stages
        Event e1 = saveEvent("Annual Tech Fest 2026",
                "A three-day technology festival featuring hackathons, coding contests, workshops, and keynote sessions from industry professionals.",
                LocalDate.of(2026, 4, 15), EventStatus.PUBLISHED, org1);

        Event e2 = saveEvent("Spring Cultural Night",
                "An evening celebrating campus culture with music, dance, and theatrical performances from student clubs.",
                LocalDate.of(2026, 4, 20), EventStatus.FACULTY_APPROVED, org1);

        Event e3 = saveEvent("Career Fair 2026",
                "Connect with recruiters from leading companies for internship and full-time placement opportunities.",
                LocalDate.of(2026, 5, 10), EventStatus.SUBMITTED, org2);

        Event e4 = saveEvent("Cloud Computing Workshop",
                "Hands-on workshop covering AWS and Azure fundamentals, containerization, and deployment pipelines.",
                LocalDate.of(2026, 5, 5), EventStatus.DRAFT, org2);

        Event e5 = saveEvent("Inter-Department Sports Day",
                "Annual sports competition across departments including cricket, football, badminton, and athletics.",
                LocalDate.of(2026, 4, 25), EventStatus.PUBLISHED, org1);

        // Participations
        saveParticipation(s1, e1);
        saveParticipation(s2, e1);
        saveParticipation(s3, e1);
        saveParticipation(s1, e5);
        saveParticipation(s2, e5);

        // Sponsorship
        saveSponsorship(sponsor, e1, 50000.00);
    }

    private User saveUser(String name, String email, String password, Role role) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(password);
        u.setRole(role);
        return userRepository.save(u);
    }

    private Event saveEvent(String title, String desc, LocalDate date, EventStatus status, User organizer) {
        Event e = new Event();
        e.setTitle(title);
        e.setDescription(desc);
        e.setDate(date);
        e.setStatus(status);
        e.setOrganizer(organizer);
        return eventRepository.save(e);
    }

    private void saveParticipation(User student, Event event) {
        Participation p = new Participation();
        p.setStudent(student);
        p.setEvent(event);
        participationRepository.save(p);
    }

    private void saveSponsorship(User sponsor, Event event, Double amount) {
        Sponsorship s = new Sponsorship();
        s.setSponsor(sponsor);
        s.setEvent(event);
        s.setAmount(amount);
        sponsorshipRepository.save(s);
    }
}
