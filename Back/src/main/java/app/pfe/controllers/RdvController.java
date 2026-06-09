package app.pfe.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.pfe.dto.RdvRequest;
import app.pfe.dto.RdvResponse;
import app.pfe.entity.Docteur;
import app.pfe.entity.Patient;
import app.pfe.entity.Rdv;
import app.pfe.service.DocteurService;
import app.pfe.service.PatientService;
import app.pfe.service.RdvService;


@RestController
@RequestMapping("/rdv")
public class RdvController {
    
    private final DocteurService docteurService;
    
    private RdvService rdvService;
    
    private final PatientService patientService;
    
    public RdvController(DocteurService docteurService,PatientService patientService,RdvService rdvService){
        this.docteurService = docteurService;
        this.patientService = patientService;
        this.rdvService = rdvService;
    }
    
    
    @PostMapping("/add")
    public ResponseEntity<?> addRdv(@RequestBody RdvRequest dto) {
        try {
            Rdv rdv = new Rdv();
            rdv.setDateRdv(dto.getDate());
            rdv.setHeureRdv(dto.getHeure());
            
            Rdv saved = rdvService.addRdv(rdv, dto.getPatientId(), dto.getDocteurId());
            
            return ResponseEntity.status(HttpStatus.CREATED)
            .body(toResponse(saved));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    
    @PutMapping("/confirm/{id}")
    public ResponseEntity<?> confirmRdv(@PathVariable int id, Authentication authentication) {
        try {
            Rdv rdv = rdvService.findById(id);
            
            String emailConnecte = authentication.getName();
            String role = authentication.getAuthorities()
            .iterator()
            .next()
            .getAuthority();
            
            boolean autorise = false;
            
            if (role.equals("ROLE_DOCTEUR")) {
                autorise = rdv.getDocteur()
                .getEmailDocteur()
                .equals(emailConnecte);
            }
            
            if (role.equals("ROLE_ADMIN")) {
                autorise = true;
            }
            
            if (!autorise) {
                return ResponseEntity.status(403)
                .body("Vous n'êtes pas autorisé à confirmer ce rendez-vous");
            }
            
            Rdv confirme = rdvService.confirmRdv(rdv);
            return ResponseEntity.ok(toResponse(confirme));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/cancel/{id}")
    public ResponseEntity<?> cancelRdv(@PathVariable int id, Authentication authentication) {
        try {
            Rdv rdv = rdvService.findById(id);
            
            String emailConnecte = authentication.getName();
            String role = authentication.getAuthorities()
            .iterator()
            .next()
            .getAuthority();
            
            boolean autorise = false;
            
            if (role.equals("ROLE_PATIENT")) {
                autorise = rdv.getPatient()
                .getEmailPatient()
                .equals(emailConnecte);
            }
            
            if (role.equals("ROLE_DOCTEUR")) {
                autorise = rdv.getDocteur()
                .getEmailDocteur()
                .equals(emailConnecte);
            }
            
            if (role.equals("ROLE_ADMIN")) {
                autorise = true;
            }
            
            if (!autorise) {
                return ResponseEntity.status(403)
                .body("Vous n'êtes pas autorisé à annuler ce rendez-vous");
            }
            
            Rdv annule = rdvService.cancelRdv(rdv);
            return ResponseEntity.ok(toResponse(annule));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    
    @PutMapping("/checkRdv")
    public ResponseEntity<String> checkRdv(@RequestBody Rdv rdv){
        String message = rdvService.checkRdv(rdv);
        return ResponseEntity.ok(message);
    }
    
    @PutMapping("/updateRdv")
    public ResponseEntity<?> updateRdv(@RequestBody RdvRequest dto, Authentication authentication) {
        try {
            Rdv oldRdv = rdvService.findById(dto.getIdRdv());
            
            String emailConnecte = authentication.getName();
            String role = authentication.getAuthorities()
            .iterator()
            .next()
            .getAuthority();
            
            boolean autorise = false;
            
            if (role.equals("ROLE_PATIENT")) {
                autorise = oldRdv.getPatient().getEmailPatient().equals(emailConnecte);
            }
            
            if (role.equals("ROLE_DOCTEUR")) {
                autorise = oldRdv.getDocteur().getEmailDocteur().equals(emailConnecte);
            }
            
            if (role.equals("ROLE_ADMIN")) {
                autorise = true;
            }
            
            if (!autorise) {
                return ResponseEntity.status(403)
                .body("Vous n'êtes pas autorisé à modifier ce rendez-vous");
            }
            
            Patient patient = patientService.getPatientById(dto.getPatientId());
            Docteur docteur = docteurService.getDocteurById(dto.getDocteurId());
            
            oldRdv.setDateRdv(dto.getDate());
            oldRdv.setHeureRdv(dto.getHeure());
            oldRdv.setStatusRdv(dto.getStatus());
            oldRdv.setPatient(patient);
            oldRdv.setDocteur(docteur);
            
            Rdv modifie = rdvService.updateRdv(oldRdv);
            return ResponseEntity.ok(toResponse(modifie));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    
    private RdvResponse toResponse(Rdv rdv) {
        return new RdvResponse(
        rdv.getIdRdv(),
        rdv.getDateRdv(),
        rdv.getHeureRdv(),
        rdv.getStatusRdv(),
        rdv.getPatient().getIdPatient(),
        rdv.getDocteur().getIdDocteur()
        );
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
