package com.example.campuseventplatform.controller;

import com.example.campuseventplatform.dto.EventCreateDTO;
import com.example.campuseventplatform.dto.StatusUpdateDTO;
import com.example.campuseventplatform.model.*;
import com.example.campuseventplatform.repository.*;
import com.example.campuseventplatform.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class WebController {

    private final EventService eventService;
    private final UserService userService;
    private final ParticipationService participationService;
    private final SponsorshipService sponsorshipService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRepository participationRepository;
    private final SponsorshipRepository sponsorshipRepository;

    public WebController(EventService eventService, UserService userService,
                         ParticipationService participationService, SponsorshipService sponsorshipService,
                         UserRepository userRepository, EventRepository eventRepository,
                         ParticipationRepository participationRepository,
                         SponsorshipRepository sponsorshipRepository) {
        this.eventService = eventService;
        this.userService = userService;
        this.participationService = participationService;
        this.sponsorshipService = sponsorshipService;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.participationRepository = participationRepository;
        this.sponsorshipRepository = sponsorshipRepository;
    }

    // ==================== AUTH ====================

    @GetMapping("/")
    public String home(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password,
                        HttpSession session, RedirectAttributes ra) {
        User user = userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(password))
                .findFirst().orElse(null);

        if (user == null) {
            ra.addFlashAttribute("error", "Invalid email or password.");
            return "redirect:/login";
        }

        session.setAttribute("user", user);
        return "redirect:/dashboard";
    }

    @GetMapping("/register")
    public String registerPage(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name, @RequestParam String email,
                           @RequestParam String password, @RequestParam String role,
                           RedirectAttributes ra) {
        boolean exists = userRepository.findAll().stream()
                .anyMatch(u -> u.getEmail().equals(email));
        if (exists) {
            ra.addFlashAttribute("error", "An account with this email already exists.");
            return "redirect:/register";
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(Role.valueOf(role));
        userService.createUser(user);

        ra.addFlashAttribute("success", "Account created successfully. Please sign in.");
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // ==================== DASHBOARDS ====================

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        return switch (user.getRole()) {
            case STUDENT -> "redirect:/dashboard/student";
            case ORGANIZER -> "redirect:/dashboard/organizer";
            case FACULTY -> "redirect:/dashboard/faculty";
            case ADMIN -> "redirect:/dashboard/admin";
            case SPONSOR -> "redirect:/dashboard/sponsor";
        };
    }

    @GetMapping("/dashboard/student")
    public String studentDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (user.getRole() != Role.STUDENT) return "redirect:/dashboard";

        List<Event> publishedEvents = eventRepository.findAll().stream()
                .filter(e -> e.getStatus() == EventStatus.PUBLISHED)
                .collect(Collectors.toList());

        List<Participation> myParticipations = participationRepository.findAll().stream()
                .filter(p -> p.getStudent().getId().equals(user.getId()))
                .collect(Collectors.toList());

        List<Long> registeredEventIds = myParticipations.stream()
                .map(p -> p.getEvent().getId())
                .collect(Collectors.toList());

        model.addAttribute("events", publishedEvents);
        model.addAttribute("participations", myParticipations);
        model.addAttribute("registeredEventIds", registeredEventIds);
        return "dashboard-student";
    }

    @GetMapping("/dashboard/organizer")
    public String organizerDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (user.getRole() != Role.ORGANIZER) return "redirect:/dashboard";

        List<Event> myEvents = eventRepository.findAll().stream()
                .filter(e -> e.getOrganizer() != null && e.getOrganizer().getId().equals(user.getId()))
                .collect(Collectors.toList());

        model.addAttribute("events", myEvents);
        return "dashboard-organizer";
    }

    @GetMapping("/dashboard/faculty")
    public String facultyDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (user.getRole() != Role.FACULTY) return "redirect:/dashboard";

        List<Event> pendingEvents = eventRepository.findAll().stream()
                .filter(e -> e.getStatus() == EventStatus.SUBMITTED)
                .collect(Collectors.toList());

        List<Event> allEvents = eventRepository.findAll();

        model.addAttribute("pendingEvents", pendingEvents);
        model.addAttribute("allEvents", allEvents);
        return "dashboard-faculty";
    }

    @GetMapping("/dashboard/admin")
    public String adminDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (user.getRole() != Role.ADMIN) return "redirect:/dashboard";

        List<Event> pendingEvents = eventRepository.findAll().stream()
                .filter(e -> e.getStatus() == EventStatus.FACULTY_APPROVED)
                .collect(Collectors.toList());

        List<Event> allEvents = eventRepository.findAll();
        List<User> allUsers = userRepository.findAll();

        model.addAttribute("pendingEvents", pendingEvents);
        model.addAttribute("allEvents", allEvents);
        model.addAttribute("totalUsers", allUsers.size());
        return "dashboard-admin";
    }

    @GetMapping("/dashboard/sponsor")
    public String sponsorDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (user.getRole() != Role.SPONSOR) return "redirect:/dashboard";

        List<Event> availableEvents = eventRepository.findAll().stream()
                .filter(e -> e.getStatus() == EventStatus.PUBLISHED
                        || e.getStatus() == EventStatus.ADMIN_APPROVED)
                .collect(Collectors.toList());

        List<Sponsorship> mySponsorships = sponsorshipRepository.findAll().stream()
                .filter(s -> s.getSponsor().getId().equals(user.getId()))
                .collect(Collectors.toList());

        model.addAttribute("events", availableEvents);
        model.addAttribute("sponsorships", mySponsorships);
        return "dashboard-sponsor";
    }

    // ==================== EVENT PAGES ====================

    @GetMapping("/events/new")
    public String createEventPage(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (user.getRole() != Role.ORGANIZER) return "redirect:/dashboard";
        return "event-create";
    }

    @PostMapping("/events/new")
    public String createEvent(@RequestParam String title, @RequestParam String description,
                              @RequestParam String date, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (user.getRole() != Role.ORGANIZER) return "redirect:/dashboard";

        EventCreateDTO eventDto = new EventCreateDTO();
        eventDto.setTitle(title);
        eventDto.setDescription(description);
        eventDto.setDate(LocalDate.parse(date));
        eventDto.setOrganizerId(user.getId());
        eventService.createEvent(eventDto);

        ra.addFlashAttribute("success", "Event created successfully.");
        return "redirect:/dashboard/organizer";
    }

    @GetMapping("/events/{id}/detail")
    public String eventDetail(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Event event = eventService.getEventById(id);
        if (event == null) return "redirect:/dashboard";

        List<Participation> participations = participationRepository.findAll().stream()
                .filter(p -> p.getEvent().getId().equals(id))
                .collect(Collectors.toList());

        List<Sponsorship> sponsorships = sponsorshipRepository.findAll().stream()
                .filter(s -> s.getEvent().getId().equals(id))
                .collect(Collectors.toList());

        boolean isRegistered = participations.stream()
                .anyMatch(p -> p.getStudent().getId().equals(user.getId()));

        model.addAttribute("event", event);
        model.addAttribute("participations", participations);
        model.addAttribute("sponsorships", sponsorships);
        model.addAttribute("isRegistered", isRegistered);
        return "event-detail";
    }

    // ==================== EVENT ACTIONS ====================

    @PostMapping("/events/{id}/submit")
    public String submitEvent(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        StatusUpdateDTO statusUpdate = new StatusUpdateDTO();
        statusUpdate.setStatus(EventStatus.SUBMITTED);
        statusUpdate.setRequestingUserId(user.getId());
        eventService.updateEventStatus(id, statusUpdate);
        ra.addFlashAttribute("success", "Event submitted for approval.");
        return "redirect:/dashboard/organizer";
    }

    @PostMapping("/events/{id}/faculty-approve")
    public String facultyApprove(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (user.getRole() != Role.FACULTY) return "redirect:/dashboard";

        StatusUpdateDTO statusUpdate = new StatusUpdateDTO();
        statusUpdate.setStatus(EventStatus.FACULTY_APPROVED);
        statusUpdate.setRequestingUserId(user.getId());
        eventService.updateEventStatus(id, statusUpdate);
        ra.addFlashAttribute("success", "Event approved by faculty.");
        return "redirect:/dashboard/faculty";
    }

    @PostMapping("/events/{id}/admin-approve")
    public String adminApprove(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (user.getRole() != Role.ADMIN) return "redirect:/dashboard";

        StatusUpdateDTO statusUpdate = new StatusUpdateDTO();
        statusUpdate.setStatus(EventStatus.ADMIN_APPROVED);
        statusUpdate.setRequestingUserId(user.getId());
        eventService.updateEventStatus(id, statusUpdate);
        ra.addFlashAttribute("success", "Event approved by admin.");
        return "redirect:/dashboard/admin";
    }

    @PostMapping("/events/{id}/publish")
    public String publishEvent(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (user.getRole() != Role.ADMIN) return "redirect:/dashboard";

        StatusUpdateDTO statusUpdate = new StatusUpdateDTO();
        statusUpdate.setStatus(EventStatus.PUBLISHED);
        statusUpdate.setRequestingUserId(user.getId());
        eventService.updateEventStatus(id, statusUpdate);
        ra.addFlashAttribute("success", "Event published successfully.");
        return "redirect:/dashboard/admin";
    }

    @PostMapping("/events/{id}/reject")
    public String rejectEvent(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        StatusUpdateDTO statusUpdate = new StatusUpdateDTO();
        statusUpdate.setStatus(EventStatus.REJECTED);
        statusUpdate.setRequestingUserId(user.getId());
        eventService.updateEventStatus(id, statusUpdate);

        ra.addFlashAttribute("success", "Event rejected.");
        return "redirect:/dashboard";
    }

    // ==================== PARTICIPATION ====================

    @PostMapping("/events/{id}/register")
    public String registerForEvent(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (user.getRole() != Role.STUDENT) return "redirect:/dashboard";

        boolean alreadyRegistered = participationRepository.findAll().stream()
                .anyMatch(p -> p.getStudent().getId().equals(user.getId())
                        && p.getEvent().getId().equals(id));

        if (alreadyRegistered) {
            ra.addFlashAttribute("error", "You are already registered for this event.");
        } else {
            participationService.registerStudent(user.getId(), id);
            ra.addFlashAttribute("success", "Successfully registered for the event.");
        }
        return "redirect:/dashboard/student";
    }

    // ==================== SPONSORSHIP ====================

    @PostMapping("/events/{id}/sponsor")
    public String sponsorEvent(@PathVariable Long id, @RequestParam Double amount,
                               HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (user.getRole() != Role.SPONSOR) return "redirect:/dashboard";

        Event event = eventRepository.findById(id).orElse(null);
        User sponsor = userRepository.findById(user.getId()).orElseThrow();

        if (event != null) {
            Sponsorship sponsorship = event.createSponsorship(sponsor, amount);
            sponsorshipService.sponsorEvent(sponsorship);
            ra.addFlashAttribute("success", "Sponsorship added successfully.");
        }
        return "redirect:/dashboard/sponsor";
    }

    // ==================== REPORTS ====================

    @GetMapping("/reports")
    public String reports(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (user.getRole() != Role.ADMIN) return "redirect:/dashboard";

        List<Event> allEvents = eventRepository.findAll();
        List<User> allUsers = userRepository.findAll();
        List<Participation> allParticipations = participationRepository.findAll();
        List<Sponsorship> allSponsorships = sponsorshipRepository.findAll();

        model.addAttribute("totalEvents", allEvents.size());
        model.addAttribute("totalUsers", allUsers.size());
        model.addAttribute("totalParticipations", allParticipations.size());
        model.addAttribute("totalSponsorshipAmount",
                allSponsorships.stream().mapToDouble(Sponsorship::getAmount).sum());

        model.addAttribute("draftCount",
                allEvents.stream().filter(e -> e.getStatus() == EventStatus.DRAFT).count());
        model.addAttribute("submittedCount",
                allEvents.stream().filter(e -> e.getStatus() == EventStatus.SUBMITTED).count());
        model.addAttribute("facultyApprovedCount",
                allEvents.stream().filter(e -> e.getStatus() == EventStatus.FACULTY_APPROVED).count());
        model.addAttribute("adminApprovedCount",
                allEvents.stream().filter(e -> e.getStatus() == EventStatus.ADMIN_APPROVED).count());
        model.addAttribute("publishedCount",
                allEvents.stream().filter(e -> e.getStatus() == EventStatus.PUBLISHED).count());
        model.addAttribute("rejectedCount",
                allEvents.stream().filter(e -> e.getStatus() == EventStatus.REJECTED).count());

        model.addAttribute("studentCount",
                allUsers.stream().filter(u -> u.getRole() == Role.STUDENT).count());
        model.addAttribute("organizerCount",
                allUsers.stream().filter(u -> u.getRole() == Role.ORGANIZER).count());
        model.addAttribute("facultyCount",
                allUsers.stream().filter(u -> u.getRole() == Role.FACULTY).count());
        model.addAttribute("adminCount",
                allUsers.stream().filter(u -> u.getRole() == Role.ADMIN).count());
        model.addAttribute("sponsorCount",
                allUsers.stream().filter(u -> u.getRole() == Role.SPONSOR).count());

        model.addAttribute("allEvents", allEvents);
        model.addAttribute("allSponsorships", allSponsorships);

        return "reports";
    }
}
