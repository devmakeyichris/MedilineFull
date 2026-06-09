package app.pfe.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import app.pfe.dto.OrdonnanceRequest;
import app.pfe.dto.OrdonnanceResponse;
import app.pfe.entity.Docteur;
import app.pfe.entity.Ordonnance;
import app.pfe.entity.Patient;
import app.pfe.service.DocteurService;
import app.pfe.service.OrdonnanceService;
import app.pfe.service.PatientService;

@RestController
@RequestMapping("/ordonnances")
public class OrdonnanceController {

    private final OrdonnanceService ordonnanceService;
    private final PatientService patientService;
    private final DocteurService docteurService;

    public OrdonnanceController(
            OrdonnanceService ordonnanceService,
            PatientService patientService,
            DocteurService docteurService) {
        this.ordonnanceService = ordonnanceService;
        this.patientService = patientService;
        this.docteurService = docteurService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addOrdonnance(
            @RequestBody OrdonnanceRequest dto,
            Authentication authentication) {
        try {
            String emailConnecte = authentication.getName();
            String role = authentication.getAuthorities()
                    .iterator()
                    .next()
                    .getAuthority();

            if (!role.equals("ROLE_DOCTEUR") && !role.equals("ROLE_ADMIN")) {
                return ResponseEntity.status(403)
                        .body("Seul un médecin peut créer une ordonnance");
            }

            Patient patient = patientService.getPatientById(dto.getPatientId());
            Docteur docteur = docteurService.getDocteurById(dto.getDocteurId());

            if (role.equals("ROLE_DOCTEUR")
                    && !docteur.getEmailDocteur().equals(emailConnecte)) {
                return ResponseEntity.status(403)
                        .body("Vous ne pouvez créer une ordonnance qu'en votre nom");
            }

            Ordonnance ordonnance = new Ordonnance();
            ordonnance.setDateOrdonnance(dto.getDateOrdonnance());
            ordonnance.setContenu(dto.getContenu());
            ordonnance.setPatient(patient);
            ordonnance.setDocteur(docteur);

            Ordonnance saved = ordonnanceService.addOrdonnance(ordonnance);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(toResponse(saved));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/patient/{id}")
    public ResponseEntity<List<OrdonnanceResponse>> getOrdonnancesByPatient(@PathVariable int id) {
        List<OrdonnanceResponse> response = ordonnanceService.getOrdonnancesByPatientId(id)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/docteur/{id}")
    public ResponseEntity<List<OrdonnanceResponse>> getOrdonnancesByDocteur(@PathVariable int id) {
        List<OrdonnanceResponse> response = ordonnanceService.getOrdonnancesByDocteurId(id)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OrdonnanceResponse>> getAllOrdonnances() {
        List<OrdonnanceResponse> response = ordonnanceService.getAllOrdonnances()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    private OrdonnanceResponse toResponse(Ordonnance ordonnance) {
        return new OrdonnanceResponse(
                ordonnance.getIdOrdonnance(),
                ordonnance.getDocteur().getNomDocteur(),
                ordonnance.getDocteur().getSpecialiteDocteur(),
                ordonnance.getPatient().getNomPatient() + " " + ordonnance.getPatient().getPrenomPatient(),
                ordonnance.getPatient().getdNaissPatient(),
                ordonnance.getDateOrdonnance(),
                ordonnance.getContenu()
        );
    }
}