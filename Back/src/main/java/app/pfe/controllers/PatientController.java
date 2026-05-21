package app.pfe.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import app.pfe.dto.RegisterPatientRequest;
import app.pfe.entity.Patient;
import app.pfe.entity.Rdv;
import app.pfe.service.PatientService;
import app.pfe.service.RdvService;

@RestController
@RequestMapping("/patients")
public class PatientController {
    
    private final PatientService patientService;
    private final RdvService rdvService;
    
    public PatientController(PatientService patientService, RdvService rdvService){
        this.patientService = patientService;
        this.rdvService = rdvService;
    }
    
    // ✅ Inscription avec DTO
    @PostMapping("/add")
    public ResponseEntity<Patient> ajouterPatient(@RequestBody RegisterPatientRequest dto) {
        try {
            Patient patient = new Patient();
            patient.setNomPatient(dto.getNom());
            patient.setPrenomPatient(dto.getPrenom());
            patient.setEmailPatient(dto.getEmail());
            patient.setTelPatient(dto.getTelephone());
            patient.setAdressePatient(dto.getAdresse());
            patient.setVillePatient(dto.getVille());
            patient.setMotDePassePatient(dto.getPassword());
            
            return ResponseEntity.status(HttpStatus.CREATED)
            .body(patientService.addPatient(patient));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Erreur : " + e.getMessage());
        }
    }
    
    // ✅ Mise à jour avec DTO
    @PutMapping("/update/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable int id, @RequestBody RegisterPatientRequest dto) {
        try {
            Patient patient = new Patient();
            patient.setNomPatient(dto.getNom());
            patient.setPrenomPatient(dto.getPrenom());
            patient.setEmailPatient(dto.getEmail());
            patient.setTelPatient(dto.getTelephone());
            patient.setAdressePatient(dto.getAdresse());
            patient.setVillePatient(dto.getVille());
            patient.setMotDePassePatient(dto.getPassword());
            
            return ResponseEntity.ok(patientService.updatePatient(id, patient));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Erreur : " + e.getMessage());
        }
    }
    
    // ✅ Les autres endpoints restent inchangés
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deletePatient(@PathVariable int id) {
        return ResponseEntity.ok(patientService.deletePatientById(id));
    }
    
    @DeleteMapping("/delete/{email}")
    public ResponseEntity<Boolean> deletePatientByEmail(@PathVariable String email) {
        return ResponseEntity.ok(patientService.deletePatientByEmail(email));
    }
    
    @GetMapping("/getAll")
    public List<Patient> getAllPatients() {
        return patientService.getAllPatient();
    }
    
    @GetMapping("/{id}/rdvs")
    public List<Rdv> getRdvsByPatient(@PathVariable int id) {
        return rdvService.getRdvsByPatientId(id);
    }
}
